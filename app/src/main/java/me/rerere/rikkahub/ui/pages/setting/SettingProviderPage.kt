package me.rerere.rikkahub.ui.pages.setting

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dokar.sonner.ToastType
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import me.rerere.ai.provider.ProviderSetting
import me.rerere.rikkahub.R
import me.rerere.rikkahub.Screen
import me.rerere.rikkahub.ui.components.ui.AutoAIIcon
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.decodeProviderSetting
import me.rerere.rikkahub.ui.components.ui.pressableScale
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.context.LocalToaster
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import me.rerere.rikkahub.ui.pages.setting.components.ProviderConfigure
import me.rerere.rikkahub.ui.theme.SourceSans3
import me.rerere.rikkahub.ui.theme.ZionGrayLight
import me.rerere.rikkahub.ui.theme.ZionSectionItem
import me.rerere.rikkahub.ui.theme.ZionSurface
import me.rerere.rikkahub.ui.theme.ZionTextPrimary
import me.rerere.rikkahub.ui.theme.ZionTextSecondary
import me.rerere.rikkahub.utils.ImageUtils
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.Uuid

private val ProviderCardGray = ZionSectionItem

private data class ProviderPresetSpec(
    val name: String,
    val createProvider: () -> ProviderSetting,
)

@Composable
fun SettingProviderPage(vm: SettingVM = koinViewModel()) {
    val settings by vm.settings.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    val toaster = LocalToaster.current
    val context = androidx.compose.ui.platform.LocalContext.current

    var showCreateSheet by remember { mutableStateOf(false) }
    var draftProvider by remember { mutableStateOf<ProviderSetting?>(null) }

    val providerPresets = remember {
        listOf(
            ProviderPresetSpec(
                name = "OpenAI",
                createProvider = { ProviderSetting.OpenAI() }
            ),
            ProviderPresetSpec(
                name = "Google",
                createProvider = { ProviderSetting.Google() }
            ),
            ProviderPresetSpec(
                name = "Claude",
                createProvider = { ProviderSetting.Claude() }
            ),
        )
    }

    val configuredPresetNames = remember(settings.providers) {
        settings.providers.map { it.name.trim().lowercase() }.toSet()
    }
    val availablePresets = remember(providerPresets, configuredPresetNames) {
        providerPresets.filterNot { preset ->
            configuredPresetNames.contains(preset.name.lowercase())
        }
    }

    fun prependProvider(provider: ProviderSetting) {
        vm.updateSettings(
            settings.copy(
                providers = listOf(provider.copyProvider(id = Uuid.random())) + settings.providers
            )
        )
    }

    val scanQrCodeLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
        handleQRResult(result, ::prependProvider, toaster, context)
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            handleImageQRCode(it, ::prependProvider, toaster, context)
        }
    }

    SettingsPage(
        title = stringResource(R.string.setting_provider_page_title),
        onBack = { navController.popBackStack() },
        trailing = {
            AddProviderActionButton(
                onClick = { showCreateSheet = true }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = PageTopBarContentTopPadding + 12.dp)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            settings.providers.forEach { provider ->
                ProviderServiceRow(
                    iconName = provider.name,
                    title = provider.name,
                    enabled = true,
                    onClick = {
                        navController.navigate(
                            Screen.SettingProviderDetail(providerId = provider.id.toString())
                        )
                    },
                    badges = emptyList()
                )
            }

            if (settings.providers.isNotEmpty() && availablePresets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = ZionGrayLight)
                Spacer(modifier = Modifier.height(4.dp))
            }

            availablePresets.forEach { preset ->
                ProviderServiceRow(
                    iconName = preset.name,
                    title = preset.name,
                    onClick = {
                        draftProvider = preset.createProvider()
                    },
                    badges = emptyList()
                )
            }
        }
    }

    if (showCreateSheet) {
        AddProviderSheet(
            presets = providerPresets,
            onDismiss = { showCreateSheet = false },
            onAddPreset = { preset ->
                showCreateSheet = false
                draftProvider = preset.createProvider()
            },
            onScanQr = {
                showCreateSheet = false
                scanQrCodeLauncher.launch(null)
            },
            onPickImage = {
                showCreateSheet = false
                pickImageLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
    }

    draftProvider?.let { provider ->
        AlertDialog(
            onDismissRequest = { draftProvider = null },
            title = {
                Text(stringResource(R.string.setting_provider_page_add_provider))
            },
            text = {
                ProviderConfigure(
                    provider = provider,
                    onEdit = { updated ->
                        draftProvider = updated
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        prependProvider(draftProvider ?: provider)
                        draftProvider = null
                    }
                ) {
                    Text(stringResource(R.string.setting_provider_page_add))
                }
            },
            dismissButton = {
                TextButton(onClick = { draftProvider = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun AddProviderActionButton(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(ZionSurface, CircleShape)
            .pressableScale(pressedScale = 0.95f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ZionAppIcons.Plus,
            contentDescription = stringResource(R.string.setting_provider_page_add_provider),
            tint = ZionTextPrimary,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun AddProviderSheet(
    presets: List<ProviderPresetSpec>,
    onDismiss: () -> Unit,
    onAddPreset: (ProviderPresetSpec) -> Unit,
    onScanQr: () -> Unit,
    onPickImage: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            presets.forEach { preset ->
                ProviderServiceRow(
                    iconName = preset.name,
                    title = preset.name,
                    onClick = { onAddPreset(preset) },
                    badges = listOf(ProviderBadge(label = "Token"))
                )
            }

            HorizontalDivider(color = ZionGrayLight)

            ProviderActionRow(
                icon = ZionAppIcons.Camera,
                title = stringResource(R.string.setting_provider_page_scan_qr_code),
                onClick = onScanQr
            )
            ProviderActionRow(
                icon = ZionAppIcons.Image,
                title = stringResource(R.string.setting_provider_page_select_from_gallery),
                onClick = onPickImage
            )
        }
    }
}

private data class ProviderBadge(
    val label: String,
)

@Composable
private fun ProviderServiceRow(
    iconName: String,
    title: String,
    enabled: Boolean = true,
    badges: List<ProviderBadge>,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(ProviderCardGray, RoundedCornerShape(20.dp))
            .pressableScale(pressedScale = 0.98f, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AutoAIIcon(
            name = iconName,
            modifier = Modifier.size(28.dp),
            color = Color.Transparent
        )

        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SourceSans3,
            color = if (enabled) ZionTextPrimary else ZionTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            badges.forEach { badge ->
                ProviderOutlineBadge(label = badge.label)
            }
        }

        Icon(
            imageVector = ZionAppIcons.ChevronRight,
            contentDescription = null,
            tint = ZionTextSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun ProviderActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(ProviderCardGray, RoundedCornerShape(20.dp))
            .pressableScale(pressedScale = 0.98f, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ZionTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SourceSans3,
            color = ZionTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = ZionAppIcons.ChevronRight,
            contentDescription = null,
            tint = ZionTextSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun ProviderOutlineBadge(label: String) {
    Box(
        modifier = Modifier
            .height(22.dp)
            .background(ProviderCardGray, RoundedCornerShape(11.dp))
            .border(1.dp, ZionTextPrimary, RoundedCornerShape(11.dp))
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontFamily = SourceSans3,
            fontWeight = FontWeight.Medium,
            color = ZionTextPrimary,
            maxLines = 1
        )
    }
}

private fun handleQRResult(
    result: QRResult,
    onAdd: (ProviderSetting) -> Unit,
    toaster: com.dokar.sonner.ToasterState,
    context: Context
) {
    runCatching {
        when (result) {
            is QRResult.QRError -> {
                toaster.show(
                    context.getString(R.string.setting_provider_page_scan_error, result),
                    type = ToastType.Error
                )
            }

            QRResult.QRMissingPermission -> {
                toaster.show(
                    context.getString(R.string.setting_provider_page_no_permission),
                    type = ToastType.Error
                )
            }

            is QRResult.QRSuccess -> {
                val setting = decodeProviderSetting(result.content.rawValue ?: "")
                onAdd(setting)
                toaster.show(
                    context.getString(R.string.setting_provider_page_import_success),
                    type = ToastType.Success
                )
            }

            QRResult.QRUserCanceled -> {}
        }
    }.onFailure { error ->
        toaster.show(
            context.getString(R.string.setting_provider_page_qr_decode_failed, error.message ?: ""),
            type = ToastType.Error
        )
    }
}

private fun handleImageQRCode(
    uri: Uri,
    onAdd: (ProviderSetting) -> Unit,
    toaster: com.dokar.sonner.ToasterState,
    context: Context
) {
    runCatching {
        val qrContent = ImageUtils.decodeQRCodeFromUri(context, uri)

        if (qrContent.isNullOrEmpty()) {
            toaster.show(
                context.getString(R.string.setting_provider_page_no_qr_found),
                type = ToastType.Error
            )
            return
        }

        val setting = decodeProviderSetting(qrContent)
        onAdd(setting)
        toaster.show(
            context.getString(R.string.setting_provider_page_import_success),
            type = ToastType.Success
        )
    }.onFailure { error ->
        toaster.show(
            context.getString(R.string.setting_provider_page_image_qr_decode_failed, error.message ?: ""),
            type = ToastType.Error
        )
    }
}
