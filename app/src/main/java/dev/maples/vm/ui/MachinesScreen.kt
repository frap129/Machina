package dev.maples.vm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.maples.vm.viewmodel.MachineViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MachinesScreen(
    navController: NavController,
    machineViewModel: MachineViewModel = koinViewModel()
) {

    val consoleVisibility = remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(30.dp),
            onClick = {
                consoleVisibility.value = true
                machineViewModel.startRootVirtualMachine()
            }
        ) {
            Text(text = "Run VM")
        }

        if (consoleVisibility.value) {
            LazyColumn(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                )
            ) {
                item {
                    val consoleText = remember { machineViewModel.consoleTextState }
                    Text(
                        text = consoleText.value,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                }
            }
        }
    }
}