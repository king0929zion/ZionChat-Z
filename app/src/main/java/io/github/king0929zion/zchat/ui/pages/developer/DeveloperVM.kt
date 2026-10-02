package io.github.king0929zion.zchat.ui.pages.developer

import androidx.lifecycle.ViewModel
import io.github.king0929zion.zchat.data.ai.AILoggingManager

class DeveloperVM(
    private val aiLoggingManager: AILoggingManager
) : ViewModel() {
    val logs = aiLoggingManager.getLogs()
}
