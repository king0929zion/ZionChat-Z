package io.github.king0929zion.zionchatz.data.event

sealed class AppEvent {
    data class Speak(val text: String) : AppEvent()
}
