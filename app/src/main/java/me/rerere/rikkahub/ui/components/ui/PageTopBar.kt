package me.rerere.rikkahub.ui.components.ui

import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.ArrowLeft01
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary

fun Modifier.headerActionButtonShadow(shape: Shape = CircleShape): Modifier = this.shadow(
    elevation = 20.dp,
    shape = shape,
    clip = false,
    ambientColor = Color.Black.copy(alpha = 0.25f),
    spotColor = Color.Black.copy(alpha = 0.18f),
)

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
fun HeaderActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .headerActionButtonShadow(CircleShape)
            .clip(CircleShape)
            .background(ZionSurface, CircleShape)
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
    containerColor: Color = ZionSurface,
    containerAlpha: Float = 0.92f,
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
                    modifier = Modifier.align(Alignment.Center)
                )

                if (trailing != null) {
                    Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                        trailing()
                    }
                }
            }
        }
    }
}
