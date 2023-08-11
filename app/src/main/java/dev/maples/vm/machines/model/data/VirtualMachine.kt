package dev.maples.vm.machines.model.data

import android.os.ParcelFileDescriptor
import android.system.virtualizationservice.DiskImage
import android.system.virtualizationservice.IVirtualMachine
import android.system.virtualizationservice.IVirtualMachineCallback
import android.system.virtualizationservice.IVirtualizationService
import android.system.virtualizationservice.VirtualMachineConfig
import android.system.virtualizationservice.VirtualMachineRawConfig
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dev.maples.vm.main.MachinaApplication
import dev.maples.vm.main.launchInBackground
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import timber.log.Timber

class VirtualMachine : KoinComponent {
    companion object {
        private const val NETWORK_SOCKET = "network.sock"
    }

    private var mRemoteShellManager: RemoteShellManager? = null
    private var mVirtualMachine: IVirtualMachine? = null

    private val mConsoleWriter: ParcelFileDescriptor
    private val mLogWriter: ParcelFileDescriptor
    private val mShellWriter: ParcelFileDescriptor
    private val mLogReader: ParcelFileDescriptor
    private val mConsoleReader: ParcelFileDescriptor
    private val mShellReader: ParcelFileDescriptor

    private val mConsoleTextState: MutableState<String> = mutableStateOf("")
    private val mShellTextState: MutableState<String> = mutableStateOf("")
    val shellTextState: State<String> = mShellTextState
    val consoleTextState: State<String> = mConsoleTextState

    init {
        System.loadLibrary("machina-jni")

        // Setup pipes
        var pipes: Array<ParcelFileDescriptor> = ParcelFileDescriptor.createPipe()
        mConsoleReader = pipes[0]
        mConsoleWriter = pipes[1]
        pipes = ParcelFileDescriptor.createPipe()
        mLogReader = pipes[0]
        mLogWriter = pipes[1]
        pipes = ParcelFileDescriptor.createPipe()
        mShellReader = pipes[0]
        mShellWriter = pipes[1]
    }

    private external fun proxyVsockToUnix(vsockFd: Int, unixSocket: String)

    fun create(virtService: IVirtualizationService) {
        mVirtualMachine = virtService.createVm(DefaultVMConfig.config, mConsoleWriter, mLogWriter)
        Timber.d("Created virtual machine: ${mVirtualMachine?.cid}")
    }

    fun start() {
        mVirtualMachine?.registerCallback(vmStateCallback)
        mVirtualMachine?.start()
        Timber.d("Started virtual machine: ${mVirtualMachine?.cid}")

        launchInBackground {
            // Start console stream
            launch {
                val console = FileInputStream(mConsoleReader.fileDescriptor)
                StreamReader("console", mConsoleTextState, console).run()
            }

            // Wait for VM to boot
            // TODO: Figure out a way to do this without just hoping its alive after a delay
            delay(2500)

            // Connect to remote shell
            try {
                mRemoteShellManager = RemoteShellManager(mVirtualMachine!!, mShellWriter)
                sendCommand("clear")

                // Start shell stream
                launch {
                    val shell = FileInputStream(mShellReader.fileDescriptor)
                    StreamReader("shell", mShellTextState, shell).run()
                }
                launch { }
            } catch (e: Exception) {
                Timber.d(e)
            }

            setupNetworking()
        }
    }

    fun stop() {
        sendCommand("poweroff")

        mConsoleTextState.value = ""
        mShellTextState.value = ""

        mVirtualMachine = null
        mRemoteShellManager = null
    }

    private fun setupNetworking() {
        val context: MachinaApplication = get()
        val netSock = File(context.filesDir, NETWORK_SOCKET)
        if (netSock.exists()) {
            netSock.delete()
        }

        launchInBackground {
            mVirtualMachine?.let {
                delay(2500)
                val networkVsock = it.connectVsock(3001)
                val gvproxy = File(context.applicationInfo.nativeLibraryDir, "libgvproxy-host.so")
                val gvproxyProcess = ProcessBuilder(
                    gvproxy.absolutePath,
                    "-debug",
                    "-listen",
                    "unix://${netSock.path}",
                    "-mtu",
                    "4000"
                ).directory(context.filesDir).redirectErrorStream(true).start()
                val error = BufferedReader(gvproxyProcess.errorStream.reader())
                val out = BufferedReader(gvproxyProcess.inputStream.reader())

                launch {
                    delay(1000)
                    proxyVsockToUnix(networkVsock.fd, netSock.path)
                }
                while (gvproxyProcess.isAlive) {
                    Timber.d(error.read().toChar().toString())
                    Timber.d(out.readLine())
                }
                Timber.d("dead")
            }
        }
    }

    fun sendCommand(cmd: String) {
        mRemoteShellManager?.apply {
            var msg = cmd
            if (cmd.isBlank()) return
            if (cmd.last() != '\n') {
                msg += '\n'
            }

            write(msg)
        }
    }

    internal class RemoteShellManager(
        virtualMachine: IVirtualMachine,
        shellWriter: ParcelFileDescriptor
    ) {
        companion object {
            const val READ_PORT = 5000
            const val WRITE_PORT = 3000
            private const val PROMPT_END = " # "
        }

        private val scope = CoroutineScope(Dispatchers.IO)
        private val writeVsock = virtualMachine.connectVsock(WRITE_PORT)
        private val readVsock = virtualMachine.connectVsock(READ_PORT)
        private val vsockReader =
            FileInputStream(readVsock.fileDescriptor).bufferedReader(Charsets.UTF_8)
        private val vsockStream = FileOutputStream(writeVsock.fileDescriptor)

        val shellStream = FileOutputStream(shellWriter.fileDescriptor)
        var state: State = State.Starting

        init {
            // Start reading
            scope.launch { readVsock() }
        }

        fun write(cmd: String) {
            state = State.Running.Writing
            shellStream.write(cmd.toByteArray())
            vsockStream.write(cmd.toByteArray())
        }

        private fun readVsock() {
            var line = ""
            while (readVsock.fileDescriptor.valid()) {
                // Set polling rate
                // delay(10)
                state = State.Running.Reading

                // Try to read from vsock
                val byte = try {
                    vsockReader.read()
                } catch (e: Exception) {
                    Timber.e(e)
                    state = State.Dead
                    return
                }

                // Ignore end of stream
                if (byte != -1) {
                    shellStream.write(byte)
                    line += byte.toChar()

                    // Log once we reach EOL or prompt
                    if (byte.toChar() == '\n' || line.endsWith(PROMPT_END)) {
                        Timber.d(line)
                        line = ""
                    }
                }
            }
        }

        sealed class State {
            object Dead : State()
            object Starting : State()
            sealed class Running : State() {
                object Reading : Running()
                object Writing : Running()
            }
        }
    }

    internal class StreamReader(
        private val mName: String,
        private val mOutput: MutableState<String>,
        private val mStream: InputStream
    ) : Runnable {
        override fun run() {
            try {
                val reader = BufferedReader(InputStreamReader(mStream))
                var char: Char
                while (reader.read().also { char = it.toChar() } != -1 && !Thread.interrupted()) {
                    mOutput.value += char

                    // Handle clear for shell streams
                    if (mOutput.value.contains("\u001B[H\u001B[J")) {
                        mOutput.value = ""
                    }
                }
            } catch (e: IOException) {
                Timber.d("Exception while posting " + mName + " output: " + e.message)
            }
        }
    }

    private val vmStateCallback = object : IVirtualMachineCallback.Stub() {
        override fun onError(cid: Int, errorCode: Int, message: String?) {
            Timber.d("CID $cid error $errorCode: $message")
        }

        override fun onDied(cid: Int, reason: Int) {
            Timber.d("CID $cid died: $reason")
        }

        // No-op for custom VMs
        override fun onPayloadStarted(cid: Int, stream: ParcelFileDescriptor?) {}
        override fun onPayloadReady(cid: Int) {}
        override fun onPayloadFinished(cid: Int, exitCode: Int) {}
    }

    private object DefaultVMConfig : VirtualMachineRawConfig() {
        const val IMAGE_DIR = "/data/local/tmp"
        const val KERNEL_PATH = "$IMAGE_DIR/kernel"
        const val ROOTFS_PATH = "$IMAGE_DIR/machina-rootfs.img"
        const val DATA_PATH = "$IMAGE_DIR/data.qcow2"
        const val SWAP_PATH = "$IMAGE_DIR/swap.qcow2"

        init {
            params =
                "panic=-1 rcu_nocbs=0-7 workqueue.power_efficient=1 root=/dev/vda rootfstype=erofs ro" +
                " init=/opt/machina/preinit console=hvc0 console=hvc2,115200"
            kernel = ParcelFileDescriptor.open(
                File(KERNEL_PATH),
                ParcelFileDescriptor.MODE_READ_ONLY
            )
            disks = arrayOf(
                // Rootfs
                DiskImage().apply {
                    writable = true
                    partitions = arrayOf()
                    image = ParcelFileDescriptor.open(
                        File(ROOTFS_PATH),
                        ParcelFileDescriptor.MODE_READ_ONLY
                    )
                },
                // Data
                DiskImage().apply {
                    writable = true
                    partitions = arrayOf()
                    image = ParcelFileDescriptor.open(
                        File(DATA_PATH),
                        ParcelFileDescriptor.MODE_READ_WRITE
                    )
                },
                // Swap
                DiskImage().apply {
                    writable = true
                    partitions = arrayOf()
                    image = ParcelFileDescriptor.open(
                        File(SWAP_PATH),
                        ParcelFileDescriptor.MODE_READ_WRITE
                    )
                }
            )
            protectedVm = false
            platformVersion = "1.0"
            taskProfiles = arrayOf("MaxPerformance", "MaxIoPriority")
            numCpus = 8
            cpuAffinity = "0-7"
        }

        val config = VirtualMachineConfig().apply { rawConfig = this@DefaultVMConfig }
    }
}
