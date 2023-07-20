package dev.maples.vm.model.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dev.maples.vm.model.services.MachinaService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class MachineRepository(private val context: Context) {
    /*
        This class wont really do anything until we can run LXCs on the host VM and retrieve their
        info. For now, expose the root vm as one of these to prove out the UI
     */


    /*
        Machina Service
     */
    private lateinit var mMachinaService: MachinaService
    private var mMachinaBound: Boolean = false
    private val mMachinaServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MachinaService.MachinaServiceBinder
            mMachinaService = binder.getService()
            mMachinaBound = true

            CoroutineScope(Dispatchers.IO).launch {
                val console = FileInputStream(mMachinaService.mConsoleReader.fileDescriptor)
                Reader("console", mConsoleTextState, console).run()
            }
            mMachinaService.startVirtualMachine()

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mMachinaBound = false
        }
    }

    private val mConsoleTextState: MutableState<String> = mutableStateOf("")
    val consoleTextState: State<String> = mConsoleTextState

    fun startMachinaService() {
        Intent(context, MachinaService::class.java).also { intent ->
            context.bindService(
                intent,
                mMachinaServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    fun stopVirtualMachine() {
        if (mMachinaBound) {
            mMachinaService.stopVirtualMachine()
        }
    }

    internal class Reader(
        private val mName: String,
        private val mOutput: MutableState<String>,
        private val mStream: InputStream
    ) : Runnable {
        override fun run() {
            try {
                val reader = BufferedReader(InputStreamReader(mStream))
                var line: String
                while (reader.readLine().also { line = it } != null && !Thread.interrupted()) {
                    mOutput.value += line + "\n"
                }
            } catch (e: IOException) {
                Timber.d("Exception while posting " + mName + " output: " + e.message)
            }
        }
    }
}