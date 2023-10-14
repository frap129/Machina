package dev.maples.vm.machines.model.repo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import dev.maples.vm.machines.model.data.VirtualMachine
import dev.maples.vm.machines.model.service.VirtualMachineService
import dev.maples.vm.main.launchInBackground
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

class MachineRepository(private val context: Context) {
    private lateinit var mVirtualMachineService: VirtualMachineService
    private var mVMServiceBound: Boolean = false

    private val _virtualMachine: MutableStateFlow<VirtualMachine?> = MutableStateFlow(null)
    val virtualMachine: StateFlow<VirtualMachine?> = _virtualMachine

    private val mMachinaServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as VirtualMachineService.MachinaServiceBinder
            mVirtualMachineService = binder.getService()
            mVMServiceBound = true

            launchInBackground {
                mVirtualMachineService.virtualMachine.collectLatest {
                    _virtualMachine.value = it
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mVMServiceBound = false
        }
    }

    init {
        // Start machina service on init
        Intent(context, VirtualMachineService::class.java).also { intent ->
            context.bindService(
                intent,
                mMachinaServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }
}
