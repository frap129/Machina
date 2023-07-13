package dev.maples.vm.model.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.os.ServiceManager
import android.system.virtualizationservice.DeathReason
import android.system.virtualizationservice.IVirtualMachineCallback
import android.system.virtualizationservice.IVirtualizationService
import dev.maples.vm.model.data.RootVirtualMachine
import org.lsposed.hiddenapibypass.HiddenApiBypass
import timber.log.Timber
import java.io.File
import kotlin.reflect.full.memberProperties

class MachinaService : Service() {
    init {
        HiddenApiBypass.addHiddenApiExemptions("")
    }

    private val binder = MachinaServiceBinder()
    override fun onBind(intent: Intent): IBinder = binder
    inner class MachinaServiceBinder : Binder() {
        fun getService(): MachinaService = this@MachinaService
    }

    private fun getVirtualizationService(): IVirtualizationService {
        val virtService = IVirtualizationService.Stub.asInterface(
            ServiceManager.waitForService("android.system.virtualizationservice")
        )
        Timber.d("Acquired virtualizationservice")

        return virtService
    }

    fun startVirtualMachine() {
        val virtService = getVirtualizationService()
        val vmConfig = RootVirtualMachine.config

        val virtualMachine = virtService.createVm(
            vmConfig,
            ParcelFileDescriptor.open(
                File("/data/local/tmp/console"),
                ParcelFileDescriptor.MODE_READ_WRITE
            ),
            ParcelFileDescriptor.open(
                File("/data/local/tmp/log"),
                ParcelFileDescriptor.MODE_READ_WRITE
            )
        )

        Timber.d("Created virtual machine: " + virtualMachine.cid)

        virtualMachine.registerCallback(rootVMCallback)
        virtualMachine.start()
    }

    private fun deathReason(reason: Int): String {
        var name = ""
        DeathReason::class.memberProperties.forEach {
            if ((it.getter.call() as Int) == reason) {
                name = it.name
                return@forEach
            }
        }
        return name
    }

    private val rootVMCallback = object : IVirtualMachineCallback.Stub() {
        override fun onError(cid: Int, errorCode: Int, message: String?) {
            Timber.d("CID $cid error $errorCode: $message")
        }

        override fun onDied(cid: Int, reason: Int) {
            Timber.d("CID $cid died: ${deathReason(reason)}")
        }

        // No-op for custom VMs
        override fun onPayloadStarted(cid: Int, stream: ParcelFileDescriptor?) {}
        override fun onPayloadReady(cid: Int) {}
        override fun onPayloadFinished(cid: Int, exitCode: Int) {}
    }

}