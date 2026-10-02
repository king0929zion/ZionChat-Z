package io.github.king0929zion.zchat.ai.provider.providers.openai

import kotlinx.coroutines.flow.Flow
import io.github.king0929zion.zchat.ai.provider.ProviderSetting
import io.github.king0929zion.zchat.ai.provider.TextGenerationParams
import io.github.king0929zion.zchat.ai.ui.MessageChunk
import io.github.king0929zion.zchat.ai.ui.UIMessage

interface OpenAIImpl {
    suspend fun generateText(
        providerSetting: ProviderSetting.OpenAI,
        messages: List<UIMessage>,
        params: TextGenerationParams,
    ): MessageChunk

    suspend fun streamText(
        providerSetting: ProviderSetting.OpenAI,
        messages: List<UIMessage>,
        params: TextGenerationParams,
    ): Flow<MessageChunk>
}
