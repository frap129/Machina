package dev.maples.vm.model.repository

import android.content.Context
import android.content.pm.IPackageManager
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.PermissionResult
import dev.maples.vm.BuildConfig
import dev.maples.vm.model.data.PermissionDenied
import dev.maples.vm.model.data.PermissionFailed
import dev.maples.vm.model.data.PermissionGranted
import dev.maples.vm.model.data.PermissionPending
import dev.maples.vm.model.data.PermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.ShizukuProvider
import rikka.shizuku.SystemServiceHelper
import timber.log.Timber


const val PERMISSION_MANAGE_VM = "android.permission.MANAGE_VIRTUAL_MACHINE"
const val PERMISSION_CUSTOM_VM = "android.permission.USE_CUSTOM_VIRTUAL_MACHINE"

class PermissionsRepository(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)

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
        PermissionPending(ShizukuProvider.PERMISSION)
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
            PermissionFailed(ShizukuProvider.PERMISSION)
        } else if (permission == PERMISSION_GRANTED) {
            PermissionGranted(ShizukuProvider.PERMISSION)
        } else {
            PermissionDenied(ShizukuProvider.PERMISSION)
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
        PermissionPending(PERMISSION_MANAGE_VM)
    )
    val manageVMPermissionState = _manageVMPermissionState.asStateFlow()
        get() {
            try {
                val result = PermissionChecker.checkSelfPermission(context, PERMISSION_MANAGE_VM)

                _manageVMPermissionState.value = when (result) {
                    PERMISSION_GRANTED -> PermissionGranted(PERMISSION_MANAGE_VM)
                    PERMISSION_DENIED -> PermissionDenied(PERMISSION_MANAGE_VM)
                    else -> PermissionFailed(PERMISSION_MANAGE_VM)
                }
            } catch (e: Exception) {
                Timber.d("Exception while getting $PERMISSION_MANAGE_VM", e)
                _manageVMPermissionState.value = PermissionFailed(PERMISSION_MANAGE_VM)
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
        PermissionPending(PERMISSION_CUSTOM_VM)
    )
    val customVMPermissionState = _customVMPermissionState.asStateFlow()
        get() {
            try {
                val result = PermissionChecker.checkSelfPermission(context, PERMISSION_CUSTOM_VM)

                _customVMPermissionState.value = when (result) {
                    PERMISSION_GRANTED -> PermissionGranted(PERMISSION_CUSTOM_VM)
                    PERMISSION_DENIED -> PermissionDenied(PERMISSION_CUSTOM_VM)
                    else -> PermissionFailed(PERMISSION_CUSTOM_VM)
                }
            } catch (e: Exception) {
                Timber.d("Exception while getting $PERMISSION_CUSTOM_VM", e)
                _customVMPermissionState.value = PermissionFailed(PERMISSION_CUSTOM_VM)
            }

            return field
        }

    fun requestCustomVMPermission(): StateFlow<PermissionState> {
        grantPermission(PERMISSION_CUSTOM_VM)
        return customVMPermissionState
    }
}