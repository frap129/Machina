package dev.maples.vm.machines.model.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.ServiceManager
import android.system.virtualizationservice.IVirtualizationService
import dev.maples.vm.machines.model.data.VirtualMachine
import org.lsposed.hiddenapibypass.HiddenApiBypass
import timber.log.Timber

class VirtualMachineService : Service() {
    init {
        HiddenApiBypass.addHiddenApiExemptions("")
    }

    private val mBinder = MachinaServiceBinder()
    private val mVirtService: IVirtualizationService = getVirtualizationService()
    var virtualMachine: VirtualMachine = createVirtualMachine()
        private set

    private fun getVirtualizationService(): IVirtualizationService {
        val virtService = IVirtualizationService.Stub.asInterface(
            ServiceManager.waitForService("android.system.virtualizationservice")
        )
        Timber.d("Acquired virtualizationservice")

        return virtService
    }

    private fun createVirtualMachine(): VirtualMachine = VirtualMachine().apply {
        create(mVirtService)
    }

    override fun onBind(intent: Intent): IBinder = mBinder
    inner class MachinaServiceBinder : Binder() {
        fun getService(): VirtualMachineService = this@VirtualMachineService
    }
}
