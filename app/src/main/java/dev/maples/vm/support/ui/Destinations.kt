package dev.maples.vm.support.ui

import dev.maples.vm.main.ui.MachinaDestination

object Destinations {
    val SupportScreen =
        MachinaDestination(
            route = "support",
            showNavBar = false,
            content = { navController, _ ->
                SupportScreen(navController = navController)
            }
        )
    val SupportScreens = listOf(SupportScreen)
}
