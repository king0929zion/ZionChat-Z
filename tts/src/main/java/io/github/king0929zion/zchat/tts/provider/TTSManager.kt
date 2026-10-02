package io.github.king0929zion.zchat.tts.provider

import android.content.Context
import kotlinx.coroutines.flow.Flow
import io.github.king0929zion.zchat.tts.model.AudioChunk
import io.github.king0929zion.zchat.tts.model.TTSRequest
import io.github.king0929zion.zchat.tts.provider.providers.GeminiTTSProvider
import io.github.king0929zion.zchat.tts.provider.providers.GroqTTSProvider
import io.github.king0929zion.zchat.tts.provider.providers.MiniMaxTTSProvider
import io.github.king0929zion.zchat.tts.provider.providers.OpenAITTSProvider
import io.github.king0929zion.zchat.tts.provider.providers.QwenTTSProvider
import io.github.king0929zion.zchat.tts.provider.providers.SystemTTSProvider

class TTSManager(private val context: Context) {
    private val openAIProvider = OpenAITTSProvider()
    private val geminiProvider = GeminiTTSProvider()
    private val systemProvider = SystemTTSProvider()
    private val miniMaxProvider = MiniMaxTTSProvider()
    private val qwenProvider = QwenTTSProvider()
    private val groqProvider = GroqTTSProvider()

    fun generateSpeech(
        providerSetting: TTSProviderSetting,
        request: TTSRequest
    ): Flow<AudioChunk> {
        return when (providerSetting) {
            is TTSProviderSetting.OpenAI -> openAIProvider.generateSpeech(context, providerSetting, request)
            is TTSProviderSetting.Gemini -> geminiProvider.generateSpeech(context, providerSetting, request)
            is TTSProviderSetting.SystemTTS -> systemProvider.generateSpeech(context, providerSetting, request)
            is TTSProviderSetting.MiniMax -> miniMaxProvider.generateSpeech(context, providerSetting, request)
            is TTSProviderSetting.Qwen -> qwenProvider.generateSpeech(context, providerSetting, request)
            is TTSProviderSetting.Groq -> groqProvider.generateSpeech(context, providerSetting, request)
        }
    }
}
