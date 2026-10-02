package io.github.king0929zion.zionchatz.ui.pages.developer

import androidx.lifecycle.ViewModel
import io.github.king0929zion.zionchatz.data.ai.AILoggingManager

class DeveloperVM(
    private val aiLoggingManager: AILoggingManager
) : ViewModel() {
    val logs = aiLoggingManager.getLogs()
}
