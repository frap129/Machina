package dev.maples.vm.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.system.virtualizationservice.DiskImage
import android.system.virtualizationservice.IVirtualizationService
import android.system.virtualizationservice.VirtualMachineConfig
import android.system.virtualizationservice.VirtualMachineRawConfig
import rikka.shizuku.ShizukuBinderWrapper
import timber.log.Timber
import java.io.File

class MachinaService : Service() {
    private val binder = MachinaServiceBinder()
    override fun onBind(intent: Intent): IBinder = binder
    inner class MachinaServiceBinder : Binder() {
        fun getService(): MachinaService = this@MachinaService
    }

    @SuppressLint("PrivateApi")
    private fun getVirtualizationService(): IVirtualizationService {
        val sm = Class.forName("android.os.ServiceManager")
        val waitForService = sm.getMethod("waitForService", String::class.java)
        val virtService = IVirtualizationService.Stub.asInterface(
            ShizukuBinderWrapper(
                waitForService.invoke(
                    null,
                    "android.system.virtualizationservice"
                ) as IBinder
            )
        )
        Timber.d("Acquired virtualizationservice")

        return virtService
    }

    private fun getVirtualMachineConfig(data: ParcelFileDescriptor): VirtualMachineConfig {
        val vmRawConfig = VirtualMachineRawConfig().apply {
            protectedVm = false
            params = "panic=-1 root=/dev/vda rootfstype=erofs ro init=/opt/machina/init"
            kernel = ParcelFileDescriptor.open(File("/data/local/tmp/kernel"), ParcelFileDescriptor.MODE_READ_ONLY)
            platformVersion = "1.0"
            disks = arrayOf(
                DiskImage().apply {
                    image = ParcelFileDescriptor.open(File("/data/local/tmp/alpine-erofs.img"), ParcelFileDescriptor.MODE_READ_ONLY)
                    //assets.openFd("alpine-rootfs.img").parcelFileDescriptor
                    writable = true
                    partitions = arrayOf()
                },
                DiskImage().apply {
                    image = data
                    writable = true
                    partitions = arrayOf()
                },
            )
            taskProfiles = arrayOf()
        }

        return VirtualMachineConfig().apply {
            rawConfig = vmRawConfig
        }
    }

    private fun startVirtualMachine() {
        val virtService = getVirtualizationService()
        val dataFile = File("/data/local/tmp/data.qcow2")
        //val data =  dataFile.createNewFile()
        val dataFd = ParcelFileDescriptor.open(dataFile, ParcelFileDescriptor.MODE_READ_WRITE)
        //virtService.initializeWritablePartition(dataFd, 10000L, PartitionType.RAW)

        val vmConfig = getVirtualMachineConfig(dataFd)

        val virtualMachine = virtService.createVm(
            vmConfig,
            ParcelFileDescriptor.open(File("/data/local/tmp/console"), ParcelFileDescriptor.MODE_READ_WRITE),
            ParcelFileDescriptor.open(File("/data/local/tmp/log"), ParcelFileDescriptor.MODE_READ_WRITE)
        )

        Timber.d("Created virtual machine: " + virtualMachine.cid)

        virtualMachine.start()
    }

}