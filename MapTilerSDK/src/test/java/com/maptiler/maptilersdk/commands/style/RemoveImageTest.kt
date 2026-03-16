/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RemoveImageTest {
    @Test
    fun removeImageToJS_CallsRemoveImage() {
        val name = "my-image"
        val command = RemoveImage(name)

        assertFalse(command.isPrimitiveReturnType)

        val js = command.toJS()

        assertEquals("${MTBridge.MAP_OBJECT}.style.removeImage('$name');", js)
    }

    @Test
    fun removeImageToJS_SanitizesName() {
        val name = "my'image\\with'quotes"
        val command = RemoveImage(name)

        val js = command.toJS()

        assertEquals("${MTBridge.MAP_OBJECT}.style.removeImage('my\\'image\\\\with\\'quotes');", js)
    }
}
