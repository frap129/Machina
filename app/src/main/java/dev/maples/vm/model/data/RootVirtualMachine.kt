package dev.maples.vm.model.data

import android.os.ParcelFileDescriptor
import android.system.virtualizationservice.DiskImage
import android.system.virtualizationservice.VirtualMachineConfig
import android.system.virtualizationservice.VirtualMachineRawConfig
import java.io.File

const val IMAGE_DIR = "/data/local/tmp"
const val KERNEL_PATH = "$IMAGE_DIR/kernel"
const val ROOTFS_PATH = "$IMAGE_DIR/alpine-erofs.img"
const val DATA_PATH = "$IMAGE_DIR/data.qcow2"
const val SWAP_PATH = "$IMAGE_DIR/swap.qcow2"

object RootVirtualMachine : VirtualMachineRawConfig() {
    init {
        params = "panic=-1 root=/dev/vda rootfstype=erofs ro init=/opt/machina/init"
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
        taskProfiles = arrayOf()
    }

    val config = VirtualMachineConfig().apply { rawConfig = this@RootVirtualMachine }
}