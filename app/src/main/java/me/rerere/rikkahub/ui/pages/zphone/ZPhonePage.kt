package me.rerere.rikkahub.ui.pages.zphone

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dokar.sonner.ToastType
import kotlinx.coroutines.delay
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.utils.navigateToChatPage

private val DesktopTextColor = Color(0xFF191919)
private val DesktopIconShape = RoundedCornerShape(22.dp)

private data class ZPhoneApp(
    val label: String,
    @DrawableRes val imageRes: Int? = null,
    val imageVector: ImageVector? = null,
    val textMark: String? = null,
    val containerColor: Color = Color.White,
    val contentColor: Color = Color(0xFF171717),
    val imagePadding: Dp = 10.dp,
    val contentScale: ContentScale = ContentScale.Fit,
    val onClick: () -> Unit,
)

@Composable
fun ZPhonePage() {
    val navController = LocalNavController.current
    val toaster = LocalToaster.current
    val apps = remember(navController, toaster) {
        listOf(
            ZPhoneApp(
                label = "OpenAI",
                imageRes = R.drawable.zphone_openai,
                imagePadding = 14.dp,
                onClick = { navigateToChatPage(navController) }
            ),
            ZPhoneApp(
                label = "微信",
                imageRes = R.drawable.zphone_xiaohongshu,
                imagePadding = 12.dp,
                onClick = { toaster.show("微信入口稍后接入", type = ToastType.Info) }
            ),
            ZPhoneApp(
                label = "X",
                imageRes = R.drawable.zphone_x_logo,
                containerColor = Color.Black,
                imagePadding = 12.dp,
                onClick = { navController.navigate(Screen.XTimeline) }
            ),
            ZPhoneApp(
                label = "ZionChat",
                textMark = "Z",
                containerColor = Color.White,
                onClick = { navigateToChatPage(navController) }
            ),
            ZPhoneApp(
                label = "图片",
                imageVector = ZionAppIcons.Image,
                containerColor = Color(0xFFF7CAD3),
                onClick = { navController.navigate(Screen.ImageGen) }
            ),
            ZPhoneApp(
                label = "文件",
                imageVector = ZionAppIcons.Files,
                containerColor = Color(0xFFE8E2D7),
                onClick = { navController.navigate(Screen.SettingFiles) }
            ),
            ZPhoneApp(
                label = "浏览器",
                imageVector = ZionAppIcons.Globe,
                containerColor = Color(0xFFC8DCF7),
                onClick = { toaster.show("浏览器入口稍后接入", type = ToastType.Info) }
            ),
            ZPhoneApp(
                label = "设置",
                imageVector = ZionAppIcons.Settings,
                containerColor = Color(0xFFF4F1EA),
                onClick = { navController.navigate(Screen.Setting) }
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.zphone_wallpaper),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.08f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 26.dp, end = 26.dp, top = 12.dp, bottom = 28.dp)
        ) {
            DesktopGrid(apps = apps)
        }
    }
}

@Composable
private fun DesktopGrid(apps: List<ZPhoneApp>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        apps.chunked(4).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                rowItems.forEachIndexed { columnIndex, app ->
                    DesktopAppIcon(
                        app = app,
                        index = rowIndex * 4 + columnIndex,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(4 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DesktopAppIcon(
    app: ZPhoneApp,
    index: Int,
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(index) {
        delay(index * 36L)
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "z_phone_icon_alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.94f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "z_phone_icon_scale"
    )

    Column(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
            scaleX = scale
            scaleY = scale
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(74.dp)
                .clip(DesktopIconShape)
                .background(app.containerColor, DesktopIconShape)
                .pressableScale(pressedScale = 0.88f, onClick = app.onClick),
            contentAlignment = Alignment.Center
        ) {
            when {
                app.imageRes != null -> {
                    Image(
                        painter = painterResource(app.imageRes),
                        contentDescription = app.label,
                        contentScale = app.contentScale,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(app.imagePadding)
                    )
                }

                app.imageVector != null -> {
                    Icon(
                        imageVector = app.imageVector,
                        contentDescription = app.label,
                        tint = app.contentColor,
                        modifier = Modifier.size(30.dp)
                    )
                }

                app.textMark != null -> {
                    Text(
                        text = app.textMark,
                        color = app.contentColor,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
        Text(
            text = app.label,
            color = DesktopTextColor,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        )
    }
}
