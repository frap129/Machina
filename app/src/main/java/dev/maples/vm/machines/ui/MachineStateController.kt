package dev.maples.vm.machines.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.maples.vm.R
import dev.maples.vm.machines.viewmodel.MachineScreenState
import dev.maples.vm.machines.viewmodel.MachineViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MachineStateController(
    modifier: Modifier = Modifier,
    machineViewModel: MachineViewModel = koinViewModel()
) {
    val state: State<MachineScreenState> = machineViewModel.machineState.collectAsState()

    Button(
        modifier = modifier.padding(10.dp, 0.dp),
        onClick = {
            when (state.value.vmRunning) {
                true -> machineViewModel.stopVirtualMachine()
                false -> machineViewModel.startVirtualMachine()
            }
        }
    ) {
        Text(
            text = stringResource(
                id = when (state.value.vmRunning) {
                    true -> R.string.stop_vm
                    false -> R.string.run_vm
                }
            )
        )
    }
}
