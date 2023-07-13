package dev.maples.vm.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.maples.vm.R
import dev.maples.vm.ui.theme.MachinaTheme
import dev.maples.vm.ui.MachinesScreen
import dev.maples.vm.viewmodel.PermissionsViewModel.MachinaPermission


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MachinaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Machina()
                }
            }
        }
    }
}

@Composable
fun Machina() {
    Column {
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 30.sp,
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterHorizontally)
        )
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "ask-permissions") {
            composable("ask-permissions") {
                PermissionsAskScreen(
                    navController = navController,
                    requestMessage = stringResource(id = R.string.shizuku_desc),
                    requestAction = stringResource(id = R.string.request_shizuku),
                )
            }
            composable("request-shizuku-permission") {
                PermissionRequestScreen(
                    navController = navController,
                    permission = MachinaPermission.SHIZUKU_PERMISSION,
                    message = stringResource(id = R.string.shizuku_pending)
                )
            }
            composable("request-manage-vm-permission") {
                PermissionRequestScreen(
                    navController = navController,
                    permission = MachinaPermission.MANAGE_VM_PERMISSION,
                    message = stringResource(id = R.string.manage_vm_pending)
                )
            }
            composable("request-custom-vm-permission") {
                PermissionRequestScreen(
                    navController = navController,
                    permission = MachinaPermission.CUSTOM_VM_PERMISSION,
                    message = stringResource(id = R.string.custom_vm_pending)
                )
            }
            composable("shizuku-denied") {
                PermissionsAskScreen(
                    navController = navController,
                    requestMessage = stringResource(id = R.string.shizuku_denied),
                    requestAction = stringResource(id = R.string.request_shizuku),
                )
            }
            composable("shizuku-failed") {
                PermissionFailedScreen()
            }
            composable("permissions-granted") {
                MachinesScreen(navController = navController)
            }
        }
    }


}
