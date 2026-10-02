package io.github.king0929zion.zchat.data.ai.transformers

import io.github.king0929zion.zchat.ai.ui.UIMessage
import io.github.king0929zion.zchat.data.files.FilesManager
import org.koin.java.KoinJavaComponent.getKoin

object Base64ImageToLocalFileTransformer : OutputMessageTransformer {
    override suspend fun onGenerationFinish(
        ctx: TransformerContext,
        messages: List<UIMessage>,
    ): List<UIMessage> {
        val filesManager = getKoin().get<FilesManager>()
        return messages.map { message ->
            filesManager.convertBase64ImagePartToLocalFile(message)
        }
    }
}
