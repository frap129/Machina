package dev.maples.vm.machines.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import dev.maples.vm.R
import dev.maples.vm.main.ui.MachinaDestination
import dev.maples.vm.main.ui.MachinaNavBarItem

object Destinations {
    val MachineLog = MachinaNavBarItem(
        route = "machines/log",
        icon = R.drawable.receipt_long,
        title = "VM Log",
        content = { _, padding ->
            MachineConsoleLogScreen(modifier = Modifier.padding(padding))
        }
    )

    val MachineShell = MachinaNavBarItem(
        route = "machines/shell",
        icon = R.drawable.terminal,
        title = "VM Shell",
        content = { _, padding ->
            MachineShellScreen(modifier = Modifier.padding(padding))
        }
    )

    val MachinesScreens = listOf<MachinaDestination>(MachineShell, MachineLog)
}
