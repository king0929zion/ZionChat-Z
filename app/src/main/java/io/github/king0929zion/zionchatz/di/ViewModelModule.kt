package io.github.king0929zion.zionchatz.di

import io.github.king0929zion.zionchatz.ui.pages.assistant.AssistantVM
import io.github.king0929zion.zionchatz.ui.pages.assistant.detail.AssistantDetailVM
import io.github.king0929zion.zionchatz.ui.pages.backup.BackupVM
import io.github.king0929zion.zionchatz.ui.pages.chat.ChatVM
import io.github.king0929zion.zionchatz.ui.pages.debug.DebugVM
import io.github.king0929zion.zionchatz.ui.pages.developer.DeveloperVM
import io.github.king0929zion.zionchatz.ui.pages.favorite.FavoriteVM
import io.github.king0929zion.zionchatz.ui.pages.search.SearchVM
import io.github.king0929zion.zionchatz.ui.pages.history.HistoryVM
import io.github.king0929zion.zionchatz.ui.pages.stats.StatsVM
import io.github.king0929zion.zionchatz.ui.pages.imggen.ImgGenVM
import io.github.king0929zion.zionchatz.ui.pages.prompts.PromptVM
import io.github.king0929zion.zionchatz.ui.pages.setting.SettingVM
import io.github.king0929zion.zionchatz.ui.pages.share.handler.ShareHandlerVM
import io.github.king0929zion.zionchatz.ui.pages.translator.TranslatorVM
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModel<ChatVM> { params ->
        ChatVM(
            id = params.get(),
            context = get(),
            settingsStore = get(),
            conversationRepo = get(),
            chatService = get(),
            updateChecker = get(),
            analytics = get(),
            filesManager = get(),
            favoriteRepository = get(),
        )
    }
    viewModelOf(::SettingVM)
    viewModelOf(::DebugVM)
    viewModelOf(::HistoryVM)
    viewModelOf(::AssistantVM)
    viewModel<AssistantDetailVM> {
        AssistantDetailVM(
            id = it.get(),
            settingsStore = get(),
            memoryRepository = get(),
            filesManager = get(),
        )
    }
    viewModelOf(::TranslatorVM)
    viewModel<ShareHandlerVM> {
        ShareHandlerVM(
            text = it.get(),
            settingsStore = get(),
        )
    }
    viewModelOf(::BackupVM)
    viewModelOf(::ImgGenVM)
    viewModelOf(::DeveloperVM)
    viewModelOf(::PromptVM)
    viewModelOf(::FavoriteVM)
    viewModelOf(::SearchVM)
    viewModelOf(::StatsVM)
}
