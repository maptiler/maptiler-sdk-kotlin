/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.net.URL

class AddSpriteTest {
    @Test
    fun `toJS returns proper string for url`() {
        val command = AddSprite("sprite1", URL("https://example.com/sprite"))
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.addSprite('sprite1', 'https://example.com/sprite');",
            command.toJS(),
        )
    }

    @Test
    fun `toJS correctly escapes quotes in identifier`() {
        val command = AddSprite("sprite'test", URL("https://example.com/sprite"))
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.addSprite('sprite\\'test', 'https://example.com/sprite');",
            command.toJS(),
        )
    }

    @Test
    fun `toJS correctly escapes backslashes in identifier`() {
        val command = AddSprite("sprite\\test", URL("https://example.com/sprite"))
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.addSprite('sprite\\\\test', 'https://example.com/sprite');",
            command.toJS(),
        )
    }

    @Test
    fun `toJS correctly formats file url`() {
        val command = AddSprite("sprite1", URL("file:///android_asset/sprite.png"))
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.addSprite('sprite1', 'file:///android_asset/sprite.png');",
            command.toJS(),
        )
    }
}
