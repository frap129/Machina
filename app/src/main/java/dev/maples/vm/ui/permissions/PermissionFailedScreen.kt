package dev.maples.vm.ui.permissions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.maples.vm.R

@Composable
fun PermissionFailedScreen() {
    Box {
        Text(
            text = stringResource(id = R.string.shizuku_failed),
            fontSize = 18.sp,
            modifier = Modifier.padding(18.dp)
        )
    }
}
