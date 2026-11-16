/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.map.types.MTMapCorner
import com.maptiler.maptilersdk.map.types.MTScaleUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AddScaleControlTest {
    @Test
    fun testIsPrimitiveReturnType() {
        val command = AddScaleControl(id = "scale1")
        assertFalse(command.isPrimitiveReturnType)
    }

    @Test
    fun testToJSWithDefaults() {
        val command = AddScaleControl(id = "scale1")
        val expectedJS =
            """
            if (!window.mapControls) window.mapControls = {};
            window.mapControls['scale1'] = new ${MTBridge.SDK_OBJECT}.ScaleControl({});
            ${MTBridge.MAP_OBJECT}.addControl(window.mapControls['scale1'], "bottom-left");
            """.trimIndent()
        assertEquals(expectedJS, command.toJS())
    }

    @Test
    fun testToJSWithAllParams() {
        val command =
            AddScaleControl(
                maxWidth = 150,
                unit = MTScaleUnit.IMPERIAL,
                position = MTMapCorner.TOP_RIGHT,
                id = "scale1",
            )
        val expectedJS =
            """
            if (!window.mapControls) window.mapControls = {};
            window.mapControls['scale1'] = new ${MTBridge.SDK_OBJECT}.ScaleControl({"maxWidth":150,"unit":"imperial"});
            ${MTBridge.MAP_OBJECT}.addControl(window.mapControls['scale1'], "top-right");
            """.trimIndent()
        assertEquals(expectedJS, command.toJS())
    }
}
