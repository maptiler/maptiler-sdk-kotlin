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

class AddAttributionControlTest {
    @Test
    fun testIsPrimitiveReturnType() {
        val command = AddAttributionControl(id = "attr1")
        assertFalse(command.isPrimitiveReturnType)
    }

    @Test
    fun testToJSWithDefaults() {
        val command = AddAttributionControl(id = "attr1")
        val expectedJS =
            """
            if (!window.mapControls) window.mapControls = {};
            window.mapControls['attr1'] = new ${MTBridge.SDK_OBJECT}.AttributionControl({});
            ${MTBridge.MAP_OBJECT}.addControl(window.mapControls['attr1'], "bottom-right");
            """.trimIndent()
        assertEquals(expectedJS, command.toJS())
    }

    @Test
    fun testToJSWithAllParams() {
        val command =
            AddAttributionControl(
                compact = true,
                customAttribution = "Custom Text",
                position = MTMapCorner.TOP_LEFT,
                id = "attr1",
            )
        val expectedJS =
            """
            if (!window.mapControls) window.mapControls = {};
            window.mapControls['attr1'] = new ${MTBridge.SDK_OBJECT}.AttributionControl({"compact":true,"customAttribution":"Custom Text"});
            ${MTBridge.MAP_OBJECT}.addControl(window.mapControls['attr1'], "top-left");
            """.trimIndent()
        assertEquals(expectedJS, command.toJS())
    }
}
