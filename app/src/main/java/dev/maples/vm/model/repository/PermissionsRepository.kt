package dev.maples.vm.model.repository

import android.content.Context
import android.content.pm.IPackageManager
import android.content.pm.PackageManager
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.PermissionResult
import dev.maples.vm.BuildConfig
import dev.maples.vm.model.data.PermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.ShizukuProvider
import rikka.shizuku.SystemServiceHelper
import timber.log.Timber


const val PERMISSION_MANAGE_VM = "android.permission.MANAGE_VIRTUAL_MACHINE"
const val PERMISSION_CUSTOM_VM = "android.permission.USE_CUSTOM_VIRTUAL_MACHINE"

class PermissionsRepository(private val context: Context) {
    init {
        HiddenApiBypass.addHiddenApiExemptions("")
    }

    private fun grantPermission(permission: String) {
        IPackageManager.Stub
            .asInterface(
                ShizukuBinderWrapper(
                    SystemServiceHelper.getSystemService("package")
                )
            )
            .grantRuntimePermission(BuildConfig.APPLICATION_ID, permission, 0)
    }

    /*
        Shizuku Permission
     */
    private val _shizukuPermissionState: MutableStateFlow<PermissionState> = MutableStateFlow(
        PermissionState.Pending
    )
    val shizukuPermissionState = _shizukuPermissionState.asStateFlow()
        get() {
            val result = when (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
                true -> PermissionChecker.checkSelfPermission(context, ShizukuProvider.PERMISSION)
                false -> Shizuku.checkSelfPermission()
            }
            _shizukuPermissionState.value = getShizukuPermissionState(result)
            return field
        }
    private val onRequestShizukuPermission =
        Shizuku.OnRequestPermissionResultListener { _, result ->
            _shizukuPermissionState.value = getShizukuPermissionState(result)
        }

    private fun getShizukuPermissionState(@PermissionResult permission: Int): PermissionState {
        return if (Shizuku.getVersion() < 10) {
            PermissionState.Failed
        } else if (permission == PERMISSION_GRANTED) {
            PermissionState.Granted
        } else {
            PermissionState.Denied
        }
    }

    fun requestShizukuPermission(): StateFlow<PermissionState> {
        Shizuku.addRequestPermissionResultListener(onRequestShizukuPermission)
        Shizuku.requestPermission(0)
        return shizukuPermissionState
    }

    /*
        MANAGE_VIRTUAL_MACHINE Permission
     */
    private val _manageVMPermissionState: MutableStateFlow<PermissionState> = MutableStateFlow(
        PermissionState.Pending
    )
    val manageVMPermissionState = _manageVMPermissionState.asStateFlow()
        get() {
            try {
                val result = PermissionChecker.checkSelfPermission(context, PERMISSION_MANAGE_VM)

                _manageVMPermissionState.value = when (result) {
                    PERMISSION_GRANTED -> PermissionState.Granted
                    PERMISSION_DENIED -> PermissionState.Denied
                    else -> PermissionState.Failed
                }
            } catch (e: Exception) {
                Timber.d("Exception while getting $PERMISSION_MANAGE_VM", e)
                _manageVMPermissionState.value = PermissionState.Failed
            }

            return field
        }

    fun requestManageVMPermission(): StateFlow<PermissionState> {
        grantPermission(PERMISSION_MANAGE_VM)
        return manageVMPermissionState
    }

    /*
        USE_CUSTOM_VIRTUAL_MACHINE Permission
     */
    private val _customVMPermissionState: MutableStateFlow<PermissionState> = MutableStateFlow(
        PermissionState.Pending
    )

    val customVMPermissionState = _customVMPermissionState.asStateFlow()
        get() {
            try {
                // Check if permission exists
                val virtualizationServiceInfo = context.packageManager.getPackageInfo(
                    "com.android.virtualmachine.res",
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
                )

                if (virtualizationServiceInfo.permissions.any { it.name == PERMISSION_CUSTOM_VM }) {
                    // Get state of permission if it exists
                    val result =
                        PermissionChecker.checkSelfPermission(context, PERMISSION_CUSTOM_VM)

                    _customVMPermissionState.value = when (result) {
                        PERMISSION_GRANTED -> PermissionState.Granted
                        PERMISSION_DENIED -> PermissionState.Denied
                        else -> PermissionState.Failed
                    }
                } else {
                    // Mark as failed if it doesn't exist
                    _customVMPermissionState.value = PermissionState.Failed
                }
            } catch (e: Exception) {
                Timber.d("Exception while getting $PERMISSION_CUSTOM_VM", e)
                _customVMPermissionState.value = PermissionState.Failed
            }

            return field
        }

    fun requestCustomVMPermission(): StateFlow<PermissionState> {
        try {
            grantPermission(PERMISSION_CUSTOM_VM)
            return customVMPermissionState
        } catch (e: Exception) {
            Timber.d("Exception while getting $PERMISSION_CUSTOM_VM", e)
        }
        return MutableStateFlow(PermissionState.Failed).asStateFlow()
    }
}
