package io.github.king0929zion.zchat.utils

import android.content.ClipData

fun ClipData.getText(): String {
    return buildString {
        repeat(itemCount) {
            append(getItemAt(it).text ?: "")
        }
    }
}
