package dev.maples.vm.ui.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import dev.maples.vm.R
import dev.maples.vm.model.data.PermissionState
import dev.maples.vm.viewmodel.PermissionsViewModel
import dev.maples.vm.viewmodel.PermissionsViewModel.MachinaPermission
import org.koin.androidx.compose.koinViewModel

@Composable
fun PermissionsScreen(
    navController: NavController,
    permissionsViewModel: PermissionsViewModel = koinViewModel()
) {
    // Request Shizuku Permission
    val shizukuPermission = permissionsViewModel
        .getPermissionState(MachinaPermission.SHIZUKU_PERMISSION)
        .collectAsState()

    when (shizukuPermission.value) {
        PermissionState.Failed -> PermissionFailedScreen()
        PermissionState.Denied, PermissionState.Pending -> RequestPermissionScreen(
            requestMessage = stringResource(id = R.string.shizuku_desc),
            buttonMessage = stringResource(id = R.string.request_shizuku),
            pendingMessage = stringResource(id = R.string.shizuku_pending),
            permission = MachinaPermission.SHIZUKU_PERMISSION
        )

        PermissionState.Granted -> {
            // Request MANAGE_VIRTUAL_MACHINE permission
            val manageVMPermission = permissionsViewModel
                .getPermissionState(MachinaPermission.MANAGE_VM_PERMISSION)
                .collectAsState()

            when (manageVMPermission.value) {
                PermissionState.Failed -> PermissionFailedScreen()
                PermissionState.Denied, PermissionState.Pending -> RequestPermissionScreen(
                    requestMessage = stringResource(id = R.string.manage_vm_desc),
                    buttonMessage = stringResource(id = R.string.allow),
                    pendingMessage = stringResource(id = R.string.manage_vm_pending),
                    permission = MachinaPermission.MANAGE_VM_PERMISSION
                )

                PermissionState.Granted -> {
                    // Request USE_CUSTOM_VIRTUAL_MACHINE
                    val customVMPermission = permissionsViewModel
                        .getPermissionState(MachinaPermission.CUSTOM_VM_PERMISSION)
                        .collectAsState()

                    when (customVMPermission.value) {
                        PermissionState.Denied, PermissionState.Pending -> RequestPermissionScreen(
                            requestMessage = stringResource(id = R.string.custom_vm_desc),
                            buttonMessage = stringResource(id = R.string.allow),
                            pendingMessage = stringResource(id = R.string.custom_vm_pending),
                            permission = MachinaPermission.CUSTOM_VM_PERMISSION
                        )

                        PermissionState.Granted, PermissionState.Failed ->
                            navController
                                .navigate("machines")
                    }
                }
            }
        }
    }
}
