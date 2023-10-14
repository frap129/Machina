package dev.maples.vm.main.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.maples.vm.R
import dev.maples.vm.machines.ui.Destinations.MachinesScreens
import dev.maples.vm.permissions.ui.Destinations.PermissionsScreens
import dev.maples.vm.support.ui.Destinations.SupportScreen
import dev.maples.vm.support.ui.Destinations.SupportScreens

@Composable
fun Machina() {
    // Collect all nav items
    val navItems = mutableListOf<MachinaDestination>()
    navItems.addAll(SupportScreens)
    navItems.addAll(PermissionsScreens)
    navItems.addAll(MachinesScreens)

    val navBarState = rememberSaveable { (mutableStateOf(false)) }
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = navBarState.value,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                content = {
                    NavigationBar {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route

                        navItems.filterIsInstance<MachinaNavBarItem>().forEach { item ->

                            NavigationBarItem(
                                selected = currentRoute == item.route,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = item.icon),
                                        contentDescription = item.title
                                    )
                                }
                            )
                        }
                    }
                }
            )
        },
        topBar = {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 30.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 0.dp, 16.dp, 0.dp)
            )
        },
        content = { padding ->
            NavHost(
                navController = navController,
                startDestination = SupportScreen.route
            ) {
                navItems.forEach { dest ->
                    composable(
                        route = dest.route,
                        content = {
                            navBarState.value = dest.showNavBar
                            dest.content(navController, padding)
                        }
                    )
                }
            }
        }
    )
}
