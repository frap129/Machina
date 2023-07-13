package dev.maples.vm.services

import android.content.pm.IPackageManager
import android.os.ParcelFileDescriptor
import android.system.virtualizationservice.IVirtualizationService
import android.system.virtualizationservice.PartitionType
import dev.maples.vm.BuildConfig
import dev.maples.vm.IShellProxyService
import rikka.shizuku.SystemServiceHelper
import kotlin.system.exitProcess

class ShellProxyService: IShellProxyService.Stub() {
    override fun destroy() { exitProcess(0) }

    override fun grantPermission(permission: String) {
        IPackageManager.Stub
            .asInterface(SystemServiceHelper.getSystemService("package"))
            .grantRuntimePermission(BuildConfig.APPLICATION_ID, permission, 0)
    }

    override fun initializeWritablePartition(
        virtService: IVirtualizationService,
        size: Long,
        fd: ParcelFileDescriptor
    ) {
        // Shell can access more directories
        virtService.initializeWritablePartition(fd, size, PartitionType.RAW)
    }
}