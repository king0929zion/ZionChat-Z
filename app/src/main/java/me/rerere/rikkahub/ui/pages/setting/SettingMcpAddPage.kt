package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.rikkahub.data.ai.mcp.McpCommonOptions
import me.rerere.rikkahub.data.ai.mcp.McpServerConfig
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.PillInput
import me.rerere.rikkahub.ui.components.ui.PillToggle
import me.rerere.rikkahub.ui.components.ui.SectionLabel
import me.rerere.rikkahub.ui.components.ui.Select
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.headerActionButtonShadow
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.components.ui.settingsBottomInsets
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionActionIcon
import me.rerere.rikkahub.ui.theme.ZionGrayLighter
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import org.koin.androidx.compose.koinViewModel

private val TRANSPORT_STREAMABLE_HTTP = "Streamable HTTP"
private val TRANSPORT_SSE = "SSE"

private val McpServerConfig.urlValue: String
    get() = when (this) {
        is McpServerConfig.SseTransportServer -> url
        is McpServerConfig.StreamableHTTPServer -> url
    }

private fun McpServerConfig.withUrl(newUrl: String): McpServerConfig = when (this) {
    is McpServerConfig.SseTransportServer -> copy(url = newUrl)
    is McpServerConfig.StreamableHTTPServer -> copy(url = newUrl)
}

private fun McpServerConfig.withCommonOptions(
    transform: McpCommonOptions.() -> McpCommonOptions
): McpServerConfig = clone(commonOptions = commonOptions.transform())

/**
 * 新建 MCP Server 页面 (设计稿: Add MCP)
 *
 * Enable 开关 / 名称 / 传输类型 (Streamable HTTP | SSE) / Endpoint URL / 自定义请求头键值对,
 * 右上角 Add 按钮随名称与 URL 的有效性启停。
 */
@Composable
fun SettingMcpAddPage(vm: SettingVM = koinViewModel()) {
    val settings by vm.settings.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    var config by remember { mutableStateOf<McpServerConfig>(McpServerConfig.StreamableHTTPServer()) }
    val valid = config.commonOptions.name.isNotBlank() && config.urlValue.isNotBlank()

    SettingsPage(
        title = "Add MCP",
        onBack = { navController.popBackStack() },
        trailing = {
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .headerActionButtonShadow(RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(ZionSurface, RoundedCornerShape(20.dp))
                    .pressableScale(enabled = valid, pressedScale = 0.95f) {
                        vm.updateSettings(
                            settings.copy(mcpServers = settings.mcpServers + config)
                        )
                        navController.popBackStack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = SourceSans3,
                    color = if (valid) ZionTextPrimary else ZionTextSecondary,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = PageTopBarContentTopPadding)
                .padding(horizontal = 16.dp)
                .settingsBottomInsets()
        ) {
            // Enable 卡片
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(ZionSectionItem)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ZionAppIcons.Tool,
                    contentDescription = null,
                    tint = ZionActionIcon,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Enable",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = SourceSans3,
                    color = ZionTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                PillToggle(
                    checked = config.commonOptions.enable,
                    onChange = { enabled ->
                        config = config.withCommonOptions { copy(enable = enabled) }
                    }
                )
            }

            SectionLabel(text = "Name")
            PillInput(
                value = config.commonOptions.name,
                onValueChange = { name ->
                    config = config.withCommonOptions { copy(name = name) }
                },
                placeholder = ""
            )

            SectionLabel(text = "Transport Type")
            Select(
                options = listOf(TRANSPORT_STREAMABLE_HTTP, TRANSPORT_SSE),
                selectedOption = when (config) {
                    is McpServerConfig.SseTransportServer -> TRANSPORT_SSE
                    is McpServerConfig.StreamableHTTPServer -> TRANSPORT_STREAMABLE_HTTP
                },
                onOptionSelected = { option ->
                    config = when (option) {
                        TRANSPORT_SSE -> {
                            if (config is McpServerConfig.SseTransportServer) config
                            else McpServerConfig.SseTransportServer(
                                commonOptions = config.commonOptions,
                                url = config.urlValue
                            )
                        }

                        else -> {
                            if (config is McpServerConfig.StreamableHTTPServer) config
                            else McpServerConfig.StreamableHTTPServer(
                                commonOptions = config.commonOptions,
                                url = config.urlValue
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            SectionLabel(text = "Endpoint URL")
            PillInput(
                value = config.urlValue,
                onValueChange = { url -> config = config.withUrl(url) },
                placeholder = "http://localhost:8888/mcp",
                keyboardType = KeyboardType.Uri
            )

            // Custom Headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 4.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Custom Headers",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = SourceSans3,
                    color = ZionTextSecondary,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .pressableScale(pressedScale = 0.95f) {
                            config = config.withCommonOptions {
                                copy(headers = headers + ("" to ""))
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = ZionAppIcons.Plus,
                        contentDescription = null,
                        tint = ZionTextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Add Header",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = SourceSans3,
                        color = ZionTextPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .animateContentSize(tween(durationMillis = 220)),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                config.commonOptions.headers.forEachIndexed { index, (_, _) ->
                    HeaderPairRow(
                        header = config.commonOptions.headers[index],
                        onChange = { pair ->
                            config = config.withCommonOptions {
                                copy(
                                    headers = headers.toMutableList().apply {
                                        set(index, pair)
                                    }
                                )
                            }
                        },
                        onRemove = {
                            config = config.withCommonOptions {
                                copy(
                                    headers = headers.toMutableList().apply {
                                        removeAt(index)
                                    }
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun HeaderPairRow(
    header: Pair<String, String>,
    onChange: (Pair<String, String>) -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderInput(
            value = header.first,
            onValueChange = { onChange(header.first to it) },
            placeholder = "Header name",
            modifier = Modifier.weight(1f)
        )
        HeaderInput(
            value = header.second,
            onValueChange = { onChange(it to header.second) },
            placeholder = "Value",
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = ZionAppIcons.Close,
            contentDescription = "Remove header",
            tint = ZionTextSecondary,
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .pressableScale(pressedScale = 0.85f, onClick = onRemove)
                .padding(5.dp)
        )
    }
}

@Composable
private fun HeaderInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 13.sp,
            fontFamily = SourceSans3,
            color = ZionTextPrimary
        ),
        cursorBrush = SolidColor(ZionTextPrimary),
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ZionGrayLighter)
    ) { innerTextField ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = 13.sp,
                    fontFamily = SourceSans3,
                    color = ZionTextSecondary
                )
            }
            innerTextField()
        }
    }
}