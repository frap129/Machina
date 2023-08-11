package dev.maples.vm.machines.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.maples.vm.machines.viewmodel.MachineViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MachineConsoleLogScreen(
    modifier: Modifier = Modifier,
    machineViewModel: MachineViewModel = koinViewModel()
) {
    MachineScreen(modifier = modifier.fillMaxHeight()) {
        LazyColumn(
            verticalArrangement = Arrangement.Bottom,
            reverseLayout = true,
            modifier = modifier
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
    }
}
