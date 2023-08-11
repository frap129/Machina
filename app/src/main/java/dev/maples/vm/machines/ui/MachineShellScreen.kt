package dev.maples.vm.machines.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.maples.vm.R
import dev.maples.vm.machines.viewmodel.MachineViewModel
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun MachineShellScreen(
    modifier: Modifier = Modifier,
    machineViewModel: MachineViewModel = koinViewModel()
) {
    MachineScreen(modifier = modifier) {
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
                    disabledIndicatorColor = Color.Transparent
                ),
                onValueChange = { commandText.value = it }
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
}
