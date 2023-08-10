package dev.maples.vm.machines.model.data

import android.os.ParcelFileDescriptor
import android.system.virtualizationservice.DiskImage
import android.system.virtualizationservice.VirtualMachineConfig
import android.system.virtualizationservice.VirtualMachineRawConfig
import java.io.File

const val IMAGE_DIR = "/data/local/tmp"
const val KERNEL_PATH = "$IMAGE_DIR/kernel"
const val ROOTFS_PATH = "$IMAGE_DIR/machina-rootfs.img"
const val DATA_PATH = "$IMAGE_DIR/data.qcow2"
const val SWAP_PATH = "$IMAGE_DIR/swap.qcow2"

object RootVirtualMachine : VirtualMachineRawConfig() {
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

    val config = VirtualMachineConfig().apply { rawConfig = this@RootVirtualMachine }
}
