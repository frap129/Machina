package dev.maples.vm.support.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.maples.vm.R
import dev.maples.vm.permissions.ui.Destinations.PermissionsScreen
import dev.maples.vm.support.viewmodel.SupportViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SupportScreen(
    navController: NavController,
    supportViewModel: SupportViewModel = koinViewModel()
) {
    val supported = supportViewModel.supportsVirtualization.collectAsState()

    if (supported.value) {
        navController.navigate(PermissionsScreen.route)
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
        ) {
            Text(
                text = stringResource(id = R.string.unsupported),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(18.dp)
            )
        }
    }
}
