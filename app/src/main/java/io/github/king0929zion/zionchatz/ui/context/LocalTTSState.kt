package io.github.king0929zion.zionchatz.ui.context

import androidx.compose.runtime.compositionLocalOf
import io.github.king0929zion.zionchatz.ui.hooks.CustomTtsState

val LocalTTSState = compositionLocalOf<CustomTtsState> { error("Not provided yet") }
