package dev.maples.vm.model.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import dev.maples.vm.model.services.MachinaService

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
            mMachinaService.startVirtualMachine()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mMachinaBound = false
        }
    }

    fun startMachinaService() {
        Intent(context, MachinaService::class.java).also { intent ->
            context.bindService(
                intent,
                mMachinaServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }
}