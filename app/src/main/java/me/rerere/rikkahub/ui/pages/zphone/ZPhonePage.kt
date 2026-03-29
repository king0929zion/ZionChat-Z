package me.rerere.rikkahub.ui.pages.zphone

import androidx.annotation.DrawableRes
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.utils.navigateToChatPage

private data class ZPhoneApp(
    val label: String,
    @DrawableRes val imageRes: Int? = null,
    val imageVector: ImageVector? = null,
    val tileColor: Color = Color.White,
    val iconTint: Color = Color(0xFF111111),
    val onClick: () -> Unit,
)

@Composable
fun ZPhonePage() {
    val navController = LocalNavController.current
    val apps = remember(navController) {
        listOf(
            ZPhoneApp("ZionChat", imageRes = R.drawable.zphone_openai, onClick = { navigateToChatPage(navController) }),
            ZPhoneApp("X", imageRes = R.drawable.zphone_x_logo, tileColor = Color.Black, onClick = { navController.navigate(Screen.XTimeline) }),
            ZPhoneApp("图片", imageVector = ZionAppIcons.Image, tileColor = Color(0xFFF6CCD2), onClick = { navController.navigate(Screen.ImageGen) }),
            ZPhoneApp("文件", imageVector = ZionAppIcons.Files, tileColor = Color(0xFFEADFD2), onClick = { navController.navigate(Screen.SettingFiles) }),
            ZPhoneApp("灵感", imageRes = R.drawable.zphone_xiaohongshu, onClick = { navController.navigate(Screen.SettingPlugins) }),
            ZPhoneApp("设置", imageVector = ZionAppIcons.Settings, tileColor = Color(0xFFF2F0EA), onClick = { navController.navigate(Screen.Setting) }),
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
                            Color.White.copy(alpha = 0.06f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.08f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp, vertical = 18.dp)
        ) {
            Text(
                text = "ZiCode",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "从这里进入内置 X 应用",
                color = Color.White.copy(alpha = 0.88f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 22.dp)
            )

            AppGrid(apps = apps)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun AppGrid(apps: List<ZPhoneApp>) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        apps.chunked(3).forEach { rowApps ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                rowApps.forEach { app ->
                    AppIconCard(app = app, modifier = Modifier.weight(1f))
                }
                repeat(3 - rowApps.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AppIconCard(
    app: ZPhoneApp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(78.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(app.tileColor)
                .pressableScale(pressedScale = 0.9f, onClick = app.onClick),
            contentAlignment = Alignment.Center
        ) {
            when {
                app.imageRes != null -> {
                    Image(
                        painter = painterResource(app.imageRes),
                        contentDescription = app.label,
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                app.imageVector != null -> {
                    Icon(
                        imageVector = app.imageVector,
                        contentDescription = app.label,
                        tint = app.iconTint,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Text(
            text = app.label,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
