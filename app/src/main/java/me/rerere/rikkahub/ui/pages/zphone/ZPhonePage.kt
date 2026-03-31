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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dokar.sonner.ToastType
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.utils.navigateToChatPage

private data class ZPhoneApp(
    val label: String,
    @DrawableRes val imageRes: Int? = null,
    val imageVector: ImageVector? = null,
    val tileColor: Color = Color.White,
    val iconTint: Color = Color(0xFF111111),
    val imagePadding: Dp = 12.dp,
    val imageContentScale: ContentScale = ContentScale.Fit,
    val onClick: () -> Unit,
)

@Composable
fun ZPhonePage() {
    val navController = LocalNavController.current
    val toaster = LocalToaster.current
    val wechatComingSoon = stringResource(R.string.zphone_wechat_coming_soon)
    val apps = listOf(
        ZPhoneApp(
            label = "ZionChat",
            imageRes = R.drawable.zphone_openai,
            imagePadding = 10.dp,
            onClick = { navigateToChatPage(navController) }
        ),
        ZPhoneApp(
            label = stringResource(R.string.zphone_app_photos),
            imageVector = ZionAppIcons.Image,
            tileColor = Color(0xFFF6CCD2),
            onClick = { navController.navigate(Screen.ImageGen) }
        ),
        ZPhoneApp(
            label = stringResource(R.string.zphone_app_files),
            imageVector = ZionAppIcons.Files,
            tileColor = Color(0xFFEADFD2),
            onClick = { navController.navigate(Screen.SettingFiles) }
        ),
        ZPhoneApp(
            label = stringResource(R.string.zphone_app_wechat),
            imageRes = R.drawable.zphone_wechat_icon,
            tileColor = Color(0xFF1AAD19),
            imagePadding = 8.dp,
            imageContentScale = ContentScale.Fit,
            onClick = {
                toaster.show(wechatComingSoon, type = ToastType.Info)
            }
        ),
        ZPhoneApp(
            label = stringResource(R.string.zphone_app_settings),
            imageVector = ZionAppIcons.Settings,
            tileColor = Color(0xFFF2F0EA),
            onClick = { navController.navigate(Screen.Setting) }
        ),
    )

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
                text = stringResource(R.string.zphone_title),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.zphone_subtitle),
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
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(app.tileColor)
                .pressableScale(pressedScale = 0.9f, onClick = app.onClick),
            contentAlignment = Alignment.Center
        ) {
            when {
                app.imageRes != null -> {
                    Image(
                        painter = painterResource(app.imageRes),
                        contentDescription = app.label,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(app.imagePadding),
                        contentScale = app.imageContentScale
                    )
                }

                app.imageVector != null -> {
                    Icon(
                        imageVector = app.imageVector,
                        contentDescription = app.label,
                        tint = app.iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Text(
            text = app.label,
            color = Color.White,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
