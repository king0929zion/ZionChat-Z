package io.github.king0929zion.zchat.data.event

sealed class AppEvent {
    data class Speak(val text: String) : AppEvent()
}
