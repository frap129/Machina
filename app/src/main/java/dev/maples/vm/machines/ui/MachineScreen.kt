package dev.maples.vm.machines.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.maples.vm.R
import dev.maples.vm.machines.viewmodel.MachineScreenState
import dev.maples.vm.machines.viewmodel.MachineViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MachineScreen(
    modifier: Modifier = Modifier,
    machineViewModel: MachineViewModel = koinViewModel(),
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier.fillMaxWidth()
    ) {
        val state: State<MachineScreenState> = machineViewModel.machineState.collectAsState()

        // Show control button once vm is created
        if (state.value.vmReady) {
            MachineStateController(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // Show placeholder message until vm is running
        if (state.value.vmReady && state.value.vmRunning) {
            content()
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
            ) {
                Text(
                    text = when (state.value.vmReady) {
                        false -> stringResource(id = R.string.creating_vm)
                        true -> stringResource(id = R.string.vm_loading)
                    },
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(18.dp)
                )
            }
        }
    }
}
