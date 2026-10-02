package io.github.king0929zion.zionchatz.tts.provider

import android.content.Context
import kotlinx.coroutines.flow.Flow
import io.github.king0929zion.zionchatz.tts.model.AudioChunk
import io.github.king0929zion.zionchatz.tts.model.TTSRequest

interface TTSProvider<T : TTSProviderSetting> {
    fun generateSpeech(
        context: Context,
        providerSetting: T,
        request: TTSRequest
    ): Flow<AudioChunk>
}
