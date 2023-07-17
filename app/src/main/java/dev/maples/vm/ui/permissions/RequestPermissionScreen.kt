package dev.maples.vm.ui.permissions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.maples.vm.viewmodel.PermissionsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RequestPermissionScreen(
    requestMessage: String,
    buttonMessage: String,
    pendingMessage: String,
    permission: PermissionsViewModel.MachinaPermission,
    permissionViewModel: PermissionsViewModel = koinViewModel()
) {
    val requestMessageState = remember { mutableStateOf(requestMessage) }
    val hasRequested = remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        when (hasRequested.value) {
            false -> {
                Text(
                    text = requestMessageState.value,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp, 80.dp)
                )

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        hasRequested.value = true
                        permissionViewModel.requestPermission(permission)
                    }
                ) {
                    Text(
                        text = buttonMessage,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            true -> {
                Text(
                    text = pendingMessage,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp, 80.dp)
                )
            }
        }
    }
}
