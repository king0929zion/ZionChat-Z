package io.github.king0929zion.zchat.ui.hooks

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import io.github.king0929zion.zchat.ui.context.LocalSharedTransitionScope

@Composable
fun Modifier.heroAnimation(
    key: Any,
): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current
    return with(sharedTransitionScope) {
        this@heroAnimation.sharedElement(
            sharedContentState = rememberSharedContentState(key),
            animatedVisibilityScope = animatedVisibilityScope
        )
    }
}
