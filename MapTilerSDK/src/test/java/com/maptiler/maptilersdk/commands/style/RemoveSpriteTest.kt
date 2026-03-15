/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RemoveSpriteTest {
    @Test
    fun `toJS returns proper string for id`() {
        val command = RemoveSprite("sprite1")
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.removeSprite('sprite1');",
            command.toJS(),
        )
    }

    @Test
    fun `toJS correctly escapes quotes in id`() {
        val command = RemoveSprite("sprite'test")
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.removeSprite('sprite\\'test');",
            command.toJS(),
        )
    }
}
