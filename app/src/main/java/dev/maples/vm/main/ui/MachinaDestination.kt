package dev.maples.vm.main.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

open class MachinaDestination(
    open val route: String,
    val showNavBar: Boolean,
    open val content: @Composable (NavController, PaddingValues) -> Unit
)

class MachinaNavBarItem(
    override val route: String,
    val icon: Int,
    val title: String,
    override val content: @Composable (NavController, PaddingValues) -> Unit
) : MachinaDestination(route, true, content)
