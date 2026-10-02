package io.github.king0929zion.zchat.ui.context

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    error("No SharedTransitionScope provided")
}
