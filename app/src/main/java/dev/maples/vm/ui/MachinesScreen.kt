package dev.maples.vm.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import dev.maples.vm.viewmodel.MachineViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MachinesScreen(
    navController: NavController,
    machineViewModel: MachineViewModel = koinViewModel()
) {
    Column {
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                machineViewModel.startRootVirtualMachine()
            }
        ) {
            Text(text = "Run VM")
        }
    }
}