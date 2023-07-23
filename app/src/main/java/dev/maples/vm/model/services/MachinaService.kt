package dev.maples.vm.model.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.os.ServiceManager
import android.system.virtualizationservice.IVirtualMachine
import android.system.virtualizationservice.IVirtualMachineCallback
import android.system.virtualizationservice.IVirtualizationService
import dev.maples.vm.model.data.RootVirtualMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.lsposed.hiddenapibypass.HiddenApiBypass
import timber.log.Timber
import java.io.FileInputStream
import java.io.FileOutputStream


class MachinaService : Service() {
    init {
        HiddenApiBypass.addHiddenApiExemptions("")
    }

    private val mBinder = MachinaServiceBinder()
    private lateinit var mVirtService: IVirtualizationService
    private var mRemoteShellManager: RemoteShellManager? = null
    private val mLogWriter: ParcelFileDescriptor

    private var mVirtualMachine: IVirtualMachine? = null
    private val mConsoleWriter: ParcelFileDescriptor

    val mLogReader: ParcelFileDescriptor
    val mConsoleReader: ParcelFileDescriptor
    val mShellText: MutableStateFlow<String> = MutableStateFlow("")

    init {
        var pipes: Array<ParcelFileDescriptor> = ParcelFileDescriptor.createPipe()
        mConsoleReader = pipes[0]
        mConsoleWriter = pipes[1]
        pipes = ParcelFileDescriptor.createPipe()
        mLogReader = pipes[0]
        mLogWriter = pipes[1]
    }

    private fun getVirtualizationService() {
        mVirtService = IVirtualizationService.Stub.asInterface(
            ServiceManager.waitForService("android.system.virtualizationservice")
        )
        Timber.d("Acquired virtualizationservice")
    }

    fun startVirtualMachine() {
        getVirtualizationService()
        val vmConfig = RootVirtualMachine.config

        mVirtualMachine = mVirtService.createVm(vmConfig, mConsoleWriter, mLogWriter)
        Timber.d("Created virtual machine: ${mVirtualMachine?.cid}")

        mVirtualMachine?.registerCallback(rootVMCallback)
        mVirtualMachine?.start()
        Timber.d("Started virtual machine: ${mVirtualMachine?.cid}")

        CoroutineScope(Dispatchers.IO).launch {
            // TODO: Figure out a way to do this without just hoping its alive after a delay
            // Wait for VM to boot
            delay(2500)
            try {
                mRemoteShellManager = RemoteShellManager(mVirtualMachine!!, mShellText)
            } catch (e: Exception) {
                Timber.d(e)
            }
        }
    }

    fun stopVirtualMachine() {
        mRemoteShellManager?.apply { write("") }
        mVirtualMachine = null
    }

    fun sendCommand(cmd: String) {
        mRemoteShellManager?.apply {
            var msg = cmd
            if (cmd.last() != '\n') {
                msg += '\n'
            }

            write(msg)
        }
    }

    private class RemoteShellManager(
        virtualMachine: IVirtualMachine,
        private val shellText: MutableStateFlow<String>
    ) {
        companion object {
            const val READ_PORT = 5000
            const val WRITE_PORT = 3000
            private const val PROMPT_END = " # "
        }

        private val writeVsock = virtualMachine.connectVsock(WRITE_PORT)
        private val readVsock = virtualMachine.connectVsock(READ_PORT)
        private val vsockReader =
            FileInputStream(readVsock.fileDescriptor).bufferedReader(Charsets.UTF_8)
        private val vsockWriter = FileOutputStream(writeVsock.fileDescriptor)
        private val scope = CoroutineScope(Dispatchers.IO)

        var state: State = State.Starting

        init {
            shellText.value = "Connecting to VM shell...\n"

            // Start reading
            scope.launch { readVsock() }
        }

        fun write(cmd: String) {
            scope.launch { writeVsock(cmd) }
        }

        private suspend fun writeVsock(cmd: String) {
            state = State.Running.Writing
            vsockWriter.write(cmd.toByteArray())
        }

        private suspend fun readVsock() {
            var line = ""
            while (readVsock.fileDescriptor.valid()) {
                // Set polling rate
                delay(10)
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
                    line += byte.toChar()

                    // Flush to state flow once we reach EOL or prompt
                    if (byte.toChar() == '\n' || line.endsWith(PROMPT_END)) {
                        Timber.d(line)
                        shellText.value += line
                        line = ""
                    }
                }
            }
        }

        sealed class State {
            object Starting : State()
            sealed class Running : State() {
                object Reading : Running()
                object Writing : Running()
            }
            object Dead : State()
        }
    }

    private val rootVMCallback = object : IVirtualMachineCallback.Stub() {
        override fun onError(cid: Int, errorCode: Int, message: String?) {
            Timber.d("CID $cid error $errorCode: $message")
        }

        override fun onDied(cid: Int, reason: Int) {
            Timber.d("CID $cid died: $reason")
        }

        // No-op for custom VMs
        override fun onPayloadStarted(cid: Int, stream: ParcelFileDescriptor?) {}
        override fun onPayloadReady(cid: Int) { Timber.d("READY")}

        override fun onPayloadFinished(cid: Int, exitCode: Int) {}
    }


    override fun onBind(intent: Intent): IBinder = mBinder
    inner class MachinaServiceBinder : Binder() {
        fun getService(): MachinaService = this@MachinaService
    }
}
