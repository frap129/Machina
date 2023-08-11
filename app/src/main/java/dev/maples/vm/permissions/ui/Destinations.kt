package dev.maples.vm.permissions.ui

import dev.maples.vm.main.ui.MachinaDestination

object Destinations {
    val PermissionsScreen =
        MachinaDestination(
            route = "permissions",
            showNavBar = false,
            content = { navController, _ ->
                PermissionsScreen(navController = navController)
            }
        )
    val PermissionsScreens = listOf(PermissionsScreen)
}
