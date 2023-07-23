package dev.maples.vm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.maples.vm.R
import dev.maples.vm.ui.theme.MachinaTheme
import dev.maples.vm.viewmodel.MachineViewModel
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MachinesScreen(
    navController: NavController,
    machineViewModel: MachineViewModel = koinViewModel(),
) {
    val consoleVisibility = remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Button(
                modifier = Modifier.padding(10.dp, 0.dp),
                onClick = {
                    consoleVisibility.value = true
                    machineViewModel.startRootVirtualMachine()
                }
            ) {
                Text(text = stringResource(id = R.string.run_vm))
            }

            if (consoleVisibility.value) {
                Button(
                    modifier = Modifier.padding(10.dp, 0.dp),
                    onClick = {
                        consoleVisibility.value = false
                        machineViewModel.stopRootVirtualMachine()
                    }
                ) {
                    Text(text = stringResource(id = R.string.stop_vm))
                }
            }
        }

        if (consoleVisibility.value) {
            val showConsole = remember { mutableStateOf(true) }

            if (showConsole.value) {
                LazyColumn(
                    verticalArrangement = Arrangement.Bottom,
                    reverseLayout = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    item {
                        val consoleText = remember { machineViewModel.consoleTextState }
                        Text(
                            text = consoleText.value,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            lineHeight = 11.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.Bottom,
                    reverseLayout = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    item {
                        val shellText = remember { machineViewModel.shellTextState }
                        Text(
                            text = shellText.value,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            lineHeight = 11.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    val commandText = remember { mutableStateOf("") }
                    TextField(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Transparent),
                        value = commandText.value,
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        onValueChange = { commandText.value = it },
                    )
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.secondary
                        ),
                        onClick = {
                            Timber.d(machineViewModel.shellTextState.value)
                            machineViewModel.sendCommand(commandText.value)
                            commandText.value = ""
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.send),
                            contentDescription = null
                        )
                    }
                }
            }


            NavigationBar {
                NavigationBarItem(
                    selected = showConsole.value,
                    onClick = { showConsole.value = true },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.receipt_long),
                            contentDescription = "Virtual Machine"
                        )
                    }
                )
                NavigationBarItem(
                    selected = !showConsole.value,
                    onClick = { showConsole.value = false },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.terminal),
                            contentDescription = "Log"
                        )
                    }
                )
            }
        }
    }
}