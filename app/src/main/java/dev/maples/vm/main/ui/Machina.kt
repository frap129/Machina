package dev.maples.vm.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.maples.vm.R
import dev.maples.vm.machines.ui.MachinesScreen
import dev.maples.vm.permissions.ui.PermissionsScreen

@Composable
fun Machina() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 30.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .align(Alignment.Start)
                .fillMaxWidth()
                .padding(24.dp, 0.dp, 16.dp, 0.dp)
        )

        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "permissions") {
            composable("permissions") { PermissionsScreen(navController = navController) }
            composable("machines") { MachinesScreen(navController = navController) }
        }
    }
}
