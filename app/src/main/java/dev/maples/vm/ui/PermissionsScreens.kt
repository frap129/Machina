package dev.maples.vm.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.maples.vm.R
import dev.maples.vm.model.data.PermissionDenied
import dev.maples.vm.model.data.PermissionFailed
import dev.maples.vm.model.data.PermissionGranted
import dev.maples.vm.model.data.PermissionPending
import dev.maples.vm.viewmodel.PermissionsViewModel
import dev.maples.vm.viewmodel.PermissionsViewModel.MachinaPermission
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PermissionsAskScreen(
    navController: NavController,
    requestMessage: String,
    requestAction: String,
) {
    val scope = rememberCoroutineScope()
    val requestMessageState = remember { mutableStateOf(requestMessage) }
    val requestActionState = remember { mutableStateOf(requestAction) }
    Column {
        Text(
            text = requestMessageState.value,
            fontSize = 18.sp,
            modifier = Modifier.padding(10.dp)
        )
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                navController.navigate("request-shizuku-permission")
            }
        ) {
            Text(
                text = requestActionState.value,
                fontSize = 10.sp,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PermissionRequestScreen(
    navController: NavController,
    permission: MachinaPermission,
    message: String,
    permissionsViewModel: PermissionsViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    Column {
        Text(
            text = message,
            fontSize = 18.sp,
            modifier = Modifier.padding(18.dp)
        )
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        val request = permissionsViewModel.requestPermission(permission)
        scope.launch {
            request.collectLatest {
                when (permission) {
                    MachinaPermission.SHIZUKU_PERMISSION -> {
                        when (it) {
                            is PermissionDenied -> navController.navigate("shizuku-denied")
                            is PermissionFailed -> navController.navigate("shizuku-failed")
                            is PermissionGranted ->
                                navController.navigate("request-manage-vm-permission")

                            is PermissionPending -> return@collectLatest
                        }
                    }

                    MachinaPermission.MANAGE_VM_PERMISSION -> {
                        when (it) {
                            is PermissionDenied -> navController.navigate("shizuku-failed")
                            is PermissionFailed -> navController.navigate("shizuku-failed")
                            is PermissionGranted ->
                                navController.navigate("request-custom-vm-permission")

                            is PermissionPending -> return@collectLatest
                        }
                    }

                    MachinaPermission.CUSTOM_VM_PERMISSION -> {
                        when (it) {
                            is PermissionDenied -> navController.navigate("shizuku-failed")
                            is PermissionFailed ->
                                navController.navigate("permissions-granted")

                            is PermissionGranted ->
                                navController.navigate("permissions-granted")

                            is PermissionPending -> return@collectLatest
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun PermissionFailedScreen() {
    Box {
        Text(
            text = stringResource(id = R.string.shizuku_failed),
            fontSize = 18.sp,
            modifier = Modifier.padding(18.dp)
        )
    }
}