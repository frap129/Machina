package dev.maples.vm.permissions.model.data

sealed class PermissionState {
    object Pending : PermissionState()
    object Failed : PermissionState()
    object Denied : PermissionState()
    object Granted : PermissionState()
}
