package io.github.king0929zion.zchat.tts.provider

import android.content.Context
import kotlinx.coroutines.flow.Flow
import io.github.king0929zion.zchat.tts.model.AudioChunk
import io.github.king0929zion.zchat.tts.model.TTSRequest

interface TTSProvider<T : TTSProviderSetting> {
    fun generateSpeech(
        context: Context,
        providerSetting: T,
        request: TTSRequest
    ): Flow<AudioChunk>
}
