package io.github.king0929zion.zchat.ui.context

import androidx.compose.runtime.compositionLocalOf
import io.github.king0929zion.zchat.ui.hooks.CustomTtsState

val LocalTTSState = compositionLocalOf<CustomTtsState> { error("Not provided yet") }
