package me.rerere.rikkahub.ui.pages.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.rerere.ai.provider.Model
import me.rerere.ai.provider.ProviderSetting
import me.rerere.rikkahub.ui.components.ui.HeaderActionButton
import me.rerere.rikkahub.ui.components.ui.PageTopBarContentTopPadding
import me.rerere.rikkahub.ui.components.ui.SettingsPage
import me.rerere.rikkahub.ui.components.ui.settingsBottomInsets
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.rikkahub.ui.icons.ZionAppIcons
import org.koin.androidx.compose.koinViewModel

/**
 * 模型配置独立页面
 *
 * 从供应商模型列表进入, 独立承载 Model ID / Name / Type / Capabilities 的编辑,
 * 右上角 ✅ 保存后写回供应商并返回。
 */
@Composable
fun SettingModelDetailPage(
    providerId: String,
    modelId: String,
    vm: SettingVM = koinViewModel(),
) {
    val navController = LocalNavController.current
    val settings by vm.settings.collectAsStateWithLifecycle()
    val provider = settings.providers.firstOrNull { it.id.toString() == providerId }
    val originalModel = provider?.models?.firstOrNull { it.id.toString() == modelId }

    if (provider == null || originalModel == null) {
        SettingsPage(
            title = "Model Settings",
            onBack = { navController.popBackStack() },
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.Text("Model not found")
            }
        }
        return
    }

    var editing by remember(modelId) { mutableStateOf(originalModel) }

    SettingsPage(
        title = "Model Settings",
        onBack = { navController.popBackStack() },
        trailing = {
            HeaderActionButton(
                onClick = {
                    vm.updateSettings(
                        settings.copy(
                            providers = settings.providers.map {
                                if (it.id == provider.id) {
                                    it.editModelSafely(editing)
                                } else {
                                    it
                                }
                            }
                        )
                    )
                    navController.popBackStack()
                },
                icon = ZionAppIcons.Check,
                contentDescription = "Save",
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(top = PageTopBarContentTopPadding, bottom = 28.dp)
                .padding(horizontal = 16.dp)
                .settingsBottomInsets(),
        ) {
            ModelSettingsForm(
                model = editing,
                onModelChange = { editing = it },
                isEdit = true,
                parentProvider = provider,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * 写回模型
 *
 * 保留原有的 editModel 语义 (按 id 替换), 新增模型时追加。
 */
private fun ProviderSetting.editModelSafely(model: Model): ProviderSetting {
    val exists = models.any { it.id == model.id }
    return if (exists) editModel(model) else addModel(model)
}
