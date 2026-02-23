package com.maptiler.maptilersdk.commands.style

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SetSpriteTest {
    @Test
    fun `toJS returns proper string for url`() {
        val command = SetSprite("https://example.com/sprite")
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.setSprite('https://example.com/sprite');",
            command.toJS(),
        )
    }

    @Test
    fun `toJS correctly escapes quotes in url`() {
        val command = SetSprite("https://example.com/sprite'test")
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.setSprite('https://example.com/sprite\\'test');",
            command.toJS(),
        )
    }
}
