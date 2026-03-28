package me.rerere.rikkahub.ui.pages.zphone

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ContentScale
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dokar.sonner.ToastType
import kotlinx.coroutines.delay
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.utils.getActivity
import me.rerere.rikkahub.utils.navigateToChatPage
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val DesktopTextColor = Color(0xFF191919)
private val StatusIconColor = Color(0xFF20201A)
private val IconShape = RoundedCornerShape(24.dp)
private val ClockFormatter = DateTimeFormatter.ofPattern("HH:mm")

private data class ZPhoneApp(
    val label: String,
    @DrawableRes val imageRes: Int? = null,
    val imageVector: ImageVector? = null,
    val containerColor: Color = Color.White,
    val iconTint: Color = Color.Unspecified,
    val imagePadding: Dp = 0.dp,
    val contentScale: ContentScale = ContentScale.Crop,
    val onClick: () -> Unit,
)

@Composable
fun ZPhonePage() {
    val navController = LocalNavController.current
    val toaster = LocalToaster.current
    val context = LocalContext.current
    val activity = remember(context) { context.getActivity() }

    DisposableEffect(activity) {
        val window = activity?.window
        if (window == null) {
            onDispose { }
        } else {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            onDispose {
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    val apps = remember(navController, toaster) {
        listOf(
            ZPhoneApp(
                label = "OpenAI",
                imageRes = R.drawable.zphone_openai,
                containerColor = Color.White.copy(alpha = 0.97f),
                imagePadding = 11.dp,
                contentScale = ContentScale.Fit,
                onClick = { navigateToChatPage(navController) }
            ),
            ZPhoneApp(
                label = "微信",
                imageRes = R.drawable.zphone_xiaohongshu,
                onClick = { toaster.show("微信入口稍后接入", type = ToastType.Info) }
            ),
            ZPhoneApp(
                label = "X",
                imageRes = R.drawable.zphone_x_logo,
                containerColor = Color.Black,
                onClick = { toaster.show("X 入口稍后接入", type = ToastType.Info) }
            ),
            ZPhoneApp(
                label = "ZionChat",
                imageRes = R.mipmap.ic_launcher,
                containerColor = Color.White,
                contentScale = ContentScale.Fit,
                onClick = { navigateToChatPage(navController) }
            ),
            ZPhoneApp(
                label = "图片",
                imageVector = ZionAppIcons.Image,
                containerColor = Color(0xFFFFD4D9),
                iconTint = DesktopTextColor,
                onClick = { navController.navigate(Screen.ImageGen) }
            ),
            ZPhoneApp(
                label = "文件",
                imageVector = ZionAppIcons.Files,
                containerColor = Color(0xFFE8E2D7),
                iconTint = DesktopTextColor,
                onClick = { navController.navigate(Screen.SettingFiles) }
            ),
            ZPhoneApp(
                label = "浏览器",
                imageVector = ZionAppIcons.Globe,
                containerColor = Color(0xFFD3E7FF),
                iconTint = DesktopTextColor,
                onClick = { toaster.show("浏览器入口稍后接入", type = ToastType.Info) }
            ),
            ZPhoneApp(
                label = "设置",
                imageVector = ZionAppIcons.Settings,
                containerColor = Color(0xFFF3EFE7),
                iconTint = DesktopTextColor,
                onClick = { navController.navigate(Screen.Setting) }
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.zphone_wallpaper),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.02f),
                            Color.Black.copy(alpha = 0.05f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.displayCutout)
                .padding(horizontal = 20.dp, top = 14.dp, bottom = 24.dp)
        ) {
            ZPhoneStatusBar(timeText = rememberClockText())
            Spacer(modifier = Modifier.height(48.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                DesktopGrid(apps = apps)
            }
        }
    }
}

@Composable
private fun rememberClockText(): String {
    var text by remember { mutableStateOf(LocalTime.now().format(ClockFormatter)) }
    LaunchedEffect(Unit) {
        while (true) {
            text = LocalTime.now().format(ClockFormatter)
            delay(1_000L)
        }
    }
    return text
}

@Composable
private fun ZPhoneStatusBar(timeText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timeText,
            color = StatusIconColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.3).sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            SignalIndicator()
            WifiIndicator()
            BatteryIndicator()
        }
    }
}

@Composable
private fun DesktopGrid(apps: List<ZPhoneApp>) {
    Column(
        modifier = Modifier.fillMaxWidth(0.92f),
        verticalArrangement = Arrangement.spacedBy(28.dp)
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
        delay(index * 45L)
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "zphone_alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.92f,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "zphone_scale"
    )
    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 18.dp,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "zphone_offset"
    )

    Column(
        modifier = modifier
            .padding(top = offsetY)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .pressableScale(pressedScale = 0.86f, onClick = app.onClick)
                .clip(IconShape),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = IconShape,
                color = app.containerColor,
                tonalElevation = 0.dp,
                shadowElevation = 10.dp,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(app.containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    AppIconContent(app = app)
                }
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.02f))
            )
        }
        Text(
            text = app.label,
            color = DesktopTextColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 13.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium.copy(
                shadow = Shadow(
                    color = Color.White.copy(alpha = 0.35f),
                    offset = Offset(0f, 1f),
                    blurRadius = 2f
                )
            ),
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun BoxScope.AppIconContent(app: ZPhoneApp) {
    if (app.imageRes != null) {
        Image(
            painter = painterResource(app.imageRes),
            contentDescription = app.label,
            contentScale = app.contentScale,
            modifier = Modifier
                .fillMaxSize()
                .padding(app.imagePadding)
        )
    } else if (app.imageVector != null) {
        Icon(
            imageVector = app.imageVector,
            contentDescription = app.label,
            tint = app.iconTint,
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun SignalIndicator() {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        listOf(4.dp, 6.dp, 8.dp, 10.dp).forEach { barHeight ->
            Box(
                modifier = Modifier
                    .size(width = 3.dp, height = barHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(StatusIconColor)
            )
        }
    }
}

@Composable
private fun WifiIndicator() {
    Canvas(modifier = Modifier.size(width = 18.dp, height = 12.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        listOf(1f, 0.74f).forEach { scale ->
            val arcSize = Size(size.width * scale, size.height * scale)
            drawArc(
                color = StatusIconColor,
                startAngle = 210f,
                sweepAngle = 120f,
                useCenter = false,
                topLeft = Offset(
                    x = (size.width - arcSize.width) / 2f,
                    y = (size.height - arcSize.height) / 2f
                ),
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        drawCircle(
            color = StatusIconColor,
            radius = 1.5.dp.toPx(),
            center = Offset(size.width / 2f, size.height - 1.7.dp.toPx())
        )
    }
}

@Composable
private fun BatteryIndicator() {
    Canvas(modifier = Modifier.size(width = 24.dp, height = 12.dp)) {
        val strokeWidth = 1.25.dp.toPx()
        val terminalWidth = 2.dp.toPx()
        drawRoundRect(
            color = StatusIconColor,
            topLeft = Offset.Zero,
            size = Size(size.width - terminalWidth - 0.5.dp.toPx(), size.height),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )
        drawRoundRect(
            color = StatusIconColor,
            topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
            size = Size(size.width - 9.dp.toPx(), size.height - 4.dp.toPx()),
            cornerRadius = CornerRadius(2.5.dp.toPx(), 2.5.dp.toPx())
        )
        drawRoundRect(
            color = StatusIconColor,
            topLeft = Offset(size.width - terminalWidth, size.height / 2f - 2.dp.toPx()),
            size = Size(terminalWidth, 4.dp.toPx()),
            cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
        )
    }
}
