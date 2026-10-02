package io.github.king0929zion.zionchatz.di

import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.remoteconfig.remoteConfig
import kotlinx.serialization.json.Json
import io.github.king0929zion.zionchatz.highlight.Highlighter
import io.github.king0929zion.zionchatz.AppScope
import io.github.king0929zion.zionchatz.data.ai.AILoggingManager
import io.github.king0929zion.zionchatz.data.ai.tools.LocalTools
import io.github.king0929zion.zionchatz.data.event.AppEventBus
import io.github.king0929zion.zionchatz.service.ChatService
import io.github.king0929zion.zionchatz.utils.EmojiData
import io.github.king0929zion.zionchatz.utils.EmojiUtils
import io.github.king0929zion.zionchatz.utils.JsonInstant
import io.github.king0929zion.zionchatz.utils.UpdateChecker
import io.github.king0929zion.zionchatz.web.WebServerManager
import io.github.king0929zion.zionchatz.tts.provider.TTSManager
import org.koin.dsl.module

val appModule = module {
    single<Json> { JsonInstant }

    single {
        Highlighter(get())
    }

    single {
        AppEventBus()
    }

    single {
        LocalTools(get(), get())
    }

    single {
        UpdateChecker(get())
    }

    single {
        AppScope()
    }

    single<EmojiData> {
        EmojiUtils.loadEmoji(get())
    }

    single {
        TTSManager(get())
    }

    single {
        Firebase.crashlytics
    }

    single {
        Firebase.remoteConfig
    }

    single {
        Firebase.analytics
    }

    single {
        AILoggingManager()
    }

    single {
        ChatService(
            context = get(),
            appScope = get(),
            settingsStore = get(),
            conversationRepo = get(),
            memoryRepository = get(),
            generationHandler = get(),
            templateTransformer = get(),
            providerManager = get(),
            localTools = get(),
            mcpManager = get(),
            filesManager = get()
        )
    }

    single {
        WebServerManager(
            context = get(),
            appScope = get(),
            chatService = get(),
            conversationRepo = get(),
            settingsStore = get(),
            filesManager = get()
        )
    }
}
