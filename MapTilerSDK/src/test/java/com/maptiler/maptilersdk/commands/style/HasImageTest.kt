package com.maptiler.maptilersdk.commands.style

import org.junit.Assert.assertEquals
import org.junit.Test

class HasImageTest {
    @Test
    fun toJS_GeneratesCorrectJS() {
        val command = HasImage("my-image")
        assertEquals(
            "map.hasImage('my-image');",
            command.toJS(),
        )
    }

    @Test
    fun toJS_SanitizesIdentifier() {
        val command = HasImage("my'image\\")
        assertEquals(
            "map.hasImage('my\\'image\\\\');",
            command.toJS(),
        )
    }
}
