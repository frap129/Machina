package dev.maples.vm.model.data

sealed class PermissionState(permission: String)
class PermissionPending(permission: String) : PermissionState(permission)
class PermissionFailed(permission: String) : PermissionState(permission)
class PermissionDenied(permission: String) : PermissionState(permission)
class PermissionGranted(permission: String) : PermissionState(permission)
