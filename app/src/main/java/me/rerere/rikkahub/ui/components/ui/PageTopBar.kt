package me.rerere.rikkahub.ui.components.ui

import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.ZionBackground
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary

val PageTopBarContentTopPadding: Dp = 72.dp

fun Modifier.headerActionButtonShadow(shape: Shape = CircleShape): Modifier = this.shadow(
    elevation = 20.dp,
    shape = shape,
    clip = false,
    ambientColor = Color.Black.copy(alpha = 0.25f),
    spotColor = Color.Black.copy(alpha = 0.18f),
)

@Composable
fun Modifier.settingsBottomInsets(): Modifier =
    this.windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))

@Composable
fun HeaderTranslucentBackdrop(
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFFFFFFF),
    containerAlpha: Float = 0.76f,
) {
    val topColor = containerColor.copy(alpha = containerAlpha.coerceIn(0.56f, 0.84f))
    val midColor = containerColor.copy(alpha = (containerAlpha * 0.54f).coerceIn(0.24f, 0.62f))

    Box(modifier = modifier) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        renderEffect = android.graphics.RenderEffect
                            .createBlurEffect(26f, 26f, Shader.TileMode.CLAMP)
                            .asComposeRenderEffect()
                    }
            )
        }

        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to topColor,
                        0.44f to topColor,
                        0.7f to midColor,
                        1.0f to Color.Transparent
                    )
                )
        )
    }
}

@Composable
fun FooterTranslucentBackdrop(
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFFFFFFF),
    containerAlpha: Float = 0.76f,
) {
    val bottomColor = containerColor.copy(alpha = containerAlpha.coerceIn(0.56f, 0.84f))
    val midColor = containerColor.copy(alpha = (containerAlpha * 0.54f).coerceIn(0.24f, 0.62f))

    Box(modifier = modifier) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        renderEffect = android.graphics.RenderEffect
                            .createBlurEffect(26f, 26f, Shader.TileMode.CLAMP)
                            .asComposeRenderEffect()
                    }
            )
        }

        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.22f to midColor,
                        0.58f to bottomColor,
                        1.0f to bottomColor
                    )
                )
        )
    }
}

@Composable
fun HeaderActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .headerActionButtonShadow(CircleShape)
            .clip(CircleShape)
            .background(Color.White, CircleShape)
            .pressableScale(pressedScale = 0.95f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = ZionTextPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun PageTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFFFFFFF),
    containerAlpha: Float = 0.78f,
    fadeHeight: Dp = 0.dp,
    trailing: (@Composable () -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        HeaderTranslucentBackdrop(
            modifier = Modifier
                .matchParentSize(),
            containerColor = containerColor,
            containerAlpha = containerAlpha
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                HeaderActionButton(
                    onClick = onBack,
                    icon = ZionAppIcons.Back,
                    contentDescription = "Back",
                    modifier = Modifier.align(Alignment.CenterStart),
                    size = 40.dp,
                )

                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SourceSans3,
                    color = ZionTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.Center)
                )

                if (trailing != null) {
                    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                        trailing()
                    }
                }
            }

            if (fadeHeight > 0.dp) {
                Spacer(modifier = Modifier.height(fadeHeight))
            }
        }
    }
}

@Composable
fun SettingsPage(
    title: String,
    onBack: () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZionBackground)
    ) {
        content()
        PageTopBar(
            title = title,
            onBack = onBack,
            trailing = trailing
        )
    }
}

@Composable
fun AutoPageTopBar(
    title: String,
    modifier: Modifier = Modifier,
    containerColor: Color = ZionSurface,
    containerAlpha: Float = 0.92f,
    fadeHeight: Dp = 0.dp,
    trailing: (@Composable () -> Unit)? = null,
) {
    val navController = LocalNavController.current
    PageTopBar(
        title = title,
        onBack = { navController.popBackStack() },
        modifier = modifier,
        containerColor = containerColor,
        containerAlpha = containerAlpha,
        fadeHeight = fadeHeight,
        trailing = trailing,
    )
}
