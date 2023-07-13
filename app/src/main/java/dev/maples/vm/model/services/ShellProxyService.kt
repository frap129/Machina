package dev.maples.vm.model.services

import android.content.pm.IPackageManager
import dev.maples.vm.BuildConfig
import dev.maples.vm.IShellProxyService
import rikka.shizuku.SystemServiceHelper
import kotlin.system.exitProcess

class ShellProxyService : IShellProxyService.Stub() {
    override fun destroy() {
        exitProcess(0)
    }

    override fun grantPermission(permission: String) {
        IPackageManager.Stub
            .asInterface(SystemServiceHelper.getSystemService("package"))
            .grantRuntimePermission(BuildConfig.APPLICATION_ID, permission, 0)
    }
}