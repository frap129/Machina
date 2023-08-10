package dev.maples.vm.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import dev.maples.vm.ui.permissions.PermissionsScreen
import dev.maples.vm.ui.theme.MachinaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MachinaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Machina()
                }
            }
        }
    }
}

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
