package dev.maples.vm.machines.model.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.ContextHidden
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.system.virtualizationservice.IVirtualizationService
import android.system.virtualmachine.VirtualMachineConfig
import android.system.virtualmachine.VirtualMachineException
import android.system.virtualmachine.VirtualMachineManager
import dev.maples.vm.machines.model.data.VirtualMachine
import kotlinx.coroutines.flow.MutableStateFlow
import org.lsposed.hiddenapibypass.HiddenApiBypass
import timber.log.Timber

private const val VICTIM_VM_NAME = "machina-victim"
private const val VIRT_SERVICE_FIELD = "mVirtualizationService"
class VirtualMachineService : Service() {
    init {
        HiddenApiBypass.addHiddenApiExemptions("")
    }

    private val mBinder = MachinaServiceBinder()
    private val mVirtService: MutableStateFlow<IVirtualizationService?> = MutableStateFlow(null)
    val virtualMachine: MutableStateFlow<VirtualMachine?> = MutableStateFlow(null)

    override fun onCreate() {
        super.onCreate()
        mVirtService.value = getVirtualizationService()
        virtualMachine.value = createVirtualMachine()
    }

    @SuppressLint("WrongConstant")
    private fun getVirtualizationService(): IVirtualizationService {
        // Create a virtual machine with the proper method
        val virtManager = getSystemService(ContextHidden.VIRTUALIZATION_SERVICE) as VirtualMachineManager
        try {
            // Delete our victim vm if it already exists
            virtManager.delete(VICTIM_VM_NAME)
        } catch (e: VirtualMachineException) {
            // Victim vm did not exist
        }
        val victim = virtManager.getOrCreate(VICTIM_VM_NAME, VirtualMachineConfig())

        // Yoink its VirtualizationService
        val wrappedVirtService = victim
            .javaClass
            .getDeclaredField(VIRT_SERVICE_FIELD)
            .apply { isAccessible = true }
            .get(victim)
        val virtService = wrappedVirtService
            .javaClass
            .getDeclaredField("mBinder")
            .apply { isAccessible = true }
            .get(wrappedVirtService) as IVirtualizationService

        Timber.d("Acquired virtualizationservice")

        return virtService
    }

    private fun createVirtualMachine(): VirtualMachine = VirtualMachine().apply {
        mVirtService.value?.let {
            create(it)
        }
    }

    override fun onBind(intent: Intent): IBinder = mBinder
    inner class MachinaServiceBinder : Binder() {
        fun getService(): VirtualMachineService = this@VirtualMachineService
    }
}
