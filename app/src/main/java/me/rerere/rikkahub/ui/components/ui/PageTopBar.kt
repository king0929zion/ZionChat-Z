package me.rerere.rikkahub.ui.components.ui

import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.ArrowLeft01
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionBackground
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary

val PageTopBarContentTopPadding: Dp = 72.dp

@Composable
fun Modifier.settingsBottomInsets(): Modifier =
    this.windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))

@Composable
fun HeaderTranslucentBackdrop(
    modifier: Modifier = Modifier,
    containerColor: Color = ZionSurface,
    containerAlpha: Float = 0.92f,
) {
    val topColor = containerColor.copy(alpha = containerAlpha.coerceIn(0.62f, 0.94f))
    val midColor = containerColor.copy(alpha = (containerAlpha * 0.66f).coerceIn(0.42f, 0.78f))

    Box(modifier = modifier) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        renderEffect = android.graphics.RenderEffect
                            .createBlurEffect(26f, 26f, Shader.TileMode.CLAMP)
                            .asComposeRenderEffect()
                    }
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to topColor,
                        0.52f to topColor,
                        0.78f to midColor,
                        1.0f to Color.Transparent
                    )
                )
        )
    }
}

@Composable
fun FooterTranslucentBackdrop(
    modifier: Modifier = Modifier,
    containerColor: Color = ZionSurface,
    containerAlpha: Float = 0.86f,
) {
    val bottomColor = containerColor.copy(alpha = containerAlpha.coerceIn(0.62f, 0.9f))
    val midColor = containerColor.copy(alpha = (containerAlpha * 0.66f).coerceIn(0.42f, 0.76f))

    Box(modifier = modifier) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        renderEffect = android.graphics.RenderEffect
                            .createBlurEffect(26f, 26f, Shader.TileMode.CLAMP)
                            .asComposeRenderEffect()
                    }
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxSize()
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
) {
    HeaderActionSurface(
        onClick = onClick,
        modifier = modifier,
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
fun HeaderActionContentButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    HeaderActionSurface(
        onClick = onClick,
        modifier = modifier,
        content = content,
    )
}

@Composable
private fun HeaderActionSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    // 使用 Surface 一次性绘制圆形背景与阴影，避免 shadow/clip/background
    // 多层叠加在圆形边缘产生发丝接缝（裂纹）
    Surface(
        modifier = modifier
            .size(40.dp)
            .pressableScale(pressedScale = 0.95f, onClick = onClick),
        shape = CircleShape,
        color = ZionSurface,
        shadowElevation = 6.dp,
        content = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = content,
            )
        }
    )
}

@Composable
fun ZionMenuIcon(
    modifier: Modifier = Modifier,
    color: Color = ZionTextPrimary,
    strokeWidth: Dp = 2.dp,
) {
    // 自绘三横线菜单图标：全部使用圆头线帽，保证线条端部为圆角且无断裂
    Canvas(modifier = modifier.size(20.dp)) {
        val strokePx = strokeWidth.toPx()
        val horizontalInset = strokePx / 2f + 1.dp.toPx()
        val verticalInset = strokePx / 2f + 2.dp.toPx()
        val left = horizontalInset
        val right = size.width - horizontalInset
        val top = verticalInset
        val bottom = size.height - verticalInset
        val centerY = (top + bottom) / 2f
        listOf(top, centerY, bottom).forEach { y ->
            drawLine(
                color = color,
                start = Offset(left, y),
                end = Offset(right, y),
                strokeWidth = strokePx,
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
fun PageTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = ZionSurface,
    containerAlpha: Float = 0.92f,
    fadeHeight: Dp = 0.dp,
    trailing: (@Composable () -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        HeaderTranslucentBackdrop(
            modifier = Modifier
                .fillMaxSize(),
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
                    icon = HugeIcons.ArrowLeft01,
                    contentDescription = "Back",
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SourceSans3,
                    color = ZionTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 72.dp)
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
