package io.github.king0929zion.zchat.ui.hooks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.king0929zion.zchat.utils.PlayStoreUtil

@Composable
fun rememberIsPlayStoreVersion(): Boolean {
    val context = LocalContext.current
    return remember {
        PlayStoreUtil.isInstalledFromPlayStore(context)
    }
}