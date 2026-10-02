package io.github.king0929zion.zionchatz.service

import org.junit.Assert.assertEquals
import org.junit.Test

class ForkConversationTitleTest {
    @Test
    fun `plain title gets suffix 1`() {
        assertEquals("Chat(1)", forkConversationTitle("Chat", emptySet()))
    }

    @Test
    fun `plain title skips existing suffix`() {
        assertEquals("Chat(2)", forkConversationTitle("Chat", setOf("Chat(1)")))
    }

    @Test
    fun `suffixed title increments instead of stacking`() {
        assertEquals("Chat(2)", forkConversationTitle("Chat(1)", emptySet()))
    }

    @Test
    fun `suffixed title skips taken numbers`() {
        assertEquals("Chat(4)", forkConversationTitle("Chat(1)", setOf("Chat(2)", "Chat(3)")))
    }

    @Test
    fun `non numeric suffix is treated as plain title`() {
        assertEquals("Chat(abc)(1)", forkConversationTitle("Chat(abc)", emptySet()))
    }
}
