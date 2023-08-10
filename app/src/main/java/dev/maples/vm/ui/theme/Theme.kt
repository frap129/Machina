package dev.maples.vm.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MachinaTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = when (isDarkTheme) {
        true -> dynamicDarkColorScheme(LocalContext.current)
        false -> dynamicLightColorScheme(LocalContext.current)
    }

    rememberSystemUiController().setStatusBarColor(colorScheme.background)

    // Make use of Material3 imports
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
