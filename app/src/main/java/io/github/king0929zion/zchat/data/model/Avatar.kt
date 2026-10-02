package io.github.king0929zion.zchat.data.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Avatar {
    @Serializable
    data object Dummy : Avatar()

    @Serializable
    data class Emoji(val content: String) : Avatar()

    @Serializable
    data class Image(val url: String) : Avatar()
}
