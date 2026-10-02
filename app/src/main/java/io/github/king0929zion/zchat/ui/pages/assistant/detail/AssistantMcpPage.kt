package io.github.king0929zion.zchat.ui.pages.assistant.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.king0929zion.zchat.R
import io.github.king0929zion.zchat.ui.components.ai.McpPicker
import io.github.king0929zion.zchat.ui.components.nav.BackButton
import io.github.king0929zion.zchat.ui.components.ui.AutoPageTopBar
import io.github.king0929zion.zchat.ui.theme.ZionBackground
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AssistantMcpPage(id: String) {
    val vm: AssistantDetailVM = koinViewModel(
        parameters = {
            parametersOf(id)
        }
    )
    val assistant by vm.assistant.collectAsStateWithLifecycle()
    val mcpServerConfigs by vm.mcpServerConfigs.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AutoPageTopBar(
                title = stringResource(R.string.assistant_page_tab_mcp)
            )
        },
        containerColor = ZionBackground,
    ) { innerPadding ->
        McpPicker(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            assistant = assistant,
            servers = mcpServerConfigs,
            onUpdateAssistant = { vm.update(it) }
        )
    }
}
