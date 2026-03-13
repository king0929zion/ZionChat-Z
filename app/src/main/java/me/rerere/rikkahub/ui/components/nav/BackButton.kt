package me.rerere.rikkahub.ui.components.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.ArrowLeft01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.ui.HeaderActionButton
import me.rerere.rikkahub.ui.context.LocalNavController

@Composable
fun BackButton(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    HeaderActionButton(
        onClick = {
            navController.popBackStack()
        },
        icon = HugeIcons.ArrowLeft01,
        contentDescription = stringResource(R.string.back),
        modifier = modifier,
    )
}
