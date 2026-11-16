/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.map.types.MTMapCorner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AddTerrainControlTest {
    @Test
    fun testToJS() {
        val command =
            AddTerrainControl(
                position = MTMapCorner.BOTTOM_LEFT,
                id = "terrain1",
            )
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            """
            if (!window.mapControls) window.mapControls = {};
            window.mapControls['terrain1'] = new ${MTBridge.SDK_OBJECT}.MaptilerTerrainControl();
            ${MTBridge.MAP_OBJECT}.addControl(window.mapControls['terrain1'], "bottom-left");
            """.trimIndent(),
            command.toJS(),
        )
    }

    @Test
    fun testToJS_default() {
        val command = AddTerrainControl(id = "terrain1")
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            """
            if (!window.mapControls) window.mapControls = {};
            window.mapControls['terrain1'] = new ${MTBridge.SDK_OBJECT}.MaptilerTerrainControl();
            ${MTBridge.MAP_OBJECT}.addControl(window.mapControls['terrain1'], "top-right");
            """.trimIndent(),
            command.toJS(),
        )
    }
}
