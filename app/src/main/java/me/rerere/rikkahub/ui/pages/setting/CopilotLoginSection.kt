package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dokar.sonner.ToastType
import me.rerere.ai.provider.ProviderSetting
import me.rerere.rikkahub.data.copilot.CopilotAuthManager
import me.rerere.rikkahub.ui.components.ui.SectionLabel
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * GitHub Copilot 设备码登录区块
 *
 * 展示登录状态, 未登录时发起设备码流程并引导用户在浏览器完成授权,
 * 授权成功后自动写入凭据并回调刷新供应商设置。
 */
@Composable
internal fun CopilotLoginSection(
    provider: ProviderSetting,
    onProviderChange: (ProviderSetting) -> Unit,
    onLoggedIn: (CopilotAuthManager.CopilotCredential) -> Unit,
    copilotAuth: CopilotAuthManager = koinInject(),
) {
    val context = LocalContext.current
    val toaster = LocalToaster.current
    val scope = rememberCoroutineScope()

    var deviceCode by remember { mutableStateOf<CopilotAuthManager.DeviceCodeInfo?>(null) }
    var polling by remember { mutableStateOf(false) }
    var loggedIn by remember(provider.id) { mutableStateOf(copilotAuth.isLoggedIn(provider.id)) }

    SectionLabel(text = "GitHub Copilot")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(ZionSectionItem, RoundedCornerShape(22.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "使用你的 GitHub Copilot 订阅",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SourceSans3,
            color = ZionTextPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (loggedIn) {
                "已连接 GitHub 账号，模型可直接使用"
            } else {
                "无需 API Key，通过设备码在浏览器中授权"
            },
            fontSize = 13.sp,
            fontFamily = SourceSans3,
            color = ZionTextSecondary,
            textAlign = TextAlign.Center,
        )

        val current = deviceCode
        if (current != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "在浏览器打开下方地址并输入验证码",
                fontSize = 12.sp,
                fontFamily = SourceSans3,
                color = ZionTextSecondary,
            )
            Spacer(modifier = Modifier.height(10.dp))
            // 验证码
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = current.userCode,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SourceSans3,
                    color = ZionTextPrimary,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            // 打开浏览器
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(ZionTextPrimary, RoundedCornerShape(22.dp))
                    .pressableScale(pressedScale = 0.97f) {
                        context.openCopilotVerificationUrl(current.verificationUri, current.userCode)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Open browser",
                    fontSize = 14.sp,
                    fontFamily = SourceSans3,
                    color = Color.White,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (polling) "等待授权中…" else "点击上方按钮开始授权",
                fontSize = 12.sp,
                fontFamily = SourceSans3,
                color = ZionTextSecondary,
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (loggedIn) {
                    SecondaryPillButton(
                        text = "Sign out",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            copilotAuth.signOut(provider.id)
                            loggedIn = false
                            onProviderChange(
                                (provider as? ProviderSetting.OpenAI)
                                    ?.copyWithApiKey("") ?: provider
                            )
                            toaster.show("已退出 GitHub Copilot", type = ToastType.Normal)
                        }
                    )
                }
                PrimaryPillButton(
                    text = if (loggedIn) "Refresh models" else "Login with GitHub",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            runCatching {
                                if (loggedIn) {
                                    copilotAuth.currentCredential(provider.id)
                                } else {
                                    val info = copilotAuth.startDeviceFlow()
                                    deviceCode = info
                                    polling = true
                                    val githubToken = copilotAuth.pollForGitHubToken(
                                        deviceCode = info.deviceCode,
                                        intervalSeconds = info.intervalSeconds,
                                        expiresInSeconds = info.expiresInSeconds,
                                        onPending = { polling = true },
                                    )
                                    val credential =
                                        copilotAuth.completeLogin(provider.id, githubToken)
                                    loggedIn = true
                                    deviceCode = null
                                    polling = false
                                    onLoggedIn(credential)
                                }
                            }.onSuccess { credential ->
                                if (credential != null) {
                                    onLoggedIn(credential)
                                    toaster.show("已连接 GitHub Copilot", type = ToastType.Success)
                                }
                            }.onFailure { error ->
                                polling = false
                                deviceCode = null
                                toaster.show(
                                    error.message ?: "GitHub 授权失败",
                                    type = ToastType.Error
                                )
                            }
                        }
                    }
                )
            }
        }
    }

}


private fun android.content.Context.openCopilotVerificationUrl(uri: String, userCode: String) {
    val url = if (uri.contains("?")) {
        "$uri&user_code=$userCode"
    } else {
        "$uri?user_code=$userCode"
    }
    runCatching {
        startActivity(
            android.content.Intent(
                android.content.Intent.ACTION_VIEW,
                android.net.Uri.parse(url)
            ).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

@Composable
private fun PrimaryPillButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(ZionTextPrimary, RoundedCornerShape(24.dp))
            .pressableScale(pressedScale = 0.97f, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SourceSans3,
            color = Color.White,
            maxLines = 1,
        )
    }
}

@Composable
private fun SecondaryPillButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(ZionGrayLighter, RoundedCornerShape(24.dp))
            .pressableScale(pressedScale = 0.97f, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SourceSans3,
            color = ZionTextPrimary,
            maxLines = 1,
        )
    }
}
