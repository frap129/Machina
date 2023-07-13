package dev.maples.vm.viewmodel

import androidx.lifecycle.ViewModel
import dev.maples.vm.model.repository.PERMISSION_CUSTOM_VM
import dev.maples.vm.model.repository.PERMISSION_MANAGE_VM
import dev.maples.vm.model.repository.PermissionsRepository
import rikka.shizuku.ShizukuProvider

class PermissionsViewModel(private val permissionsRepo: PermissionsRepository) : ViewModel() {
    enum class MachinaPermission(val permission: String) {
        SHIZUKU_PERMISSION(ShizukuProvider.PERMISSION),
        MANAGE_VM_PERMISSION(PERMISSION_MANAGE_VM),
        CUSTOM_VM_PERMISSION(PERMISSION_CUSTOM_VM)
    }

    fun getPermissionState(permission: MachinaPermission) = when (permission) {
        MachinaPermission.SHIZUKU_PERMISSION -> permissionsRepo.shizukuPermissionState
        MachinaPermission.MANAGE_VM_PERMISSION -> permissionsRepo.manageVMPermissionState
        MachinaPermission.CUSTOM_VM_PERMISSION -> permissionsRepo.customVMPermissionState
    }

    fun requestPermission(permission: MachinaPermission) = when (permission) {
        MachinaPermission.SHIZUKU_PERMISSION -> permissionsRepo.requestShizukuPermission()
        MachinaPermission.MANAGE_VM_PERMISSION -> permissionsRepo.requestManageVMPermission()
        MachinaPermission.CUSTOM_VM_PERMISSION -> permissionsRepo.requestCustomVMPermission()
    }
}