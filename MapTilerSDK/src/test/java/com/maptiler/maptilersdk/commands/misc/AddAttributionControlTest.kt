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
        val command = AddAttributionControl()
        assertFalse(command.isPrimitiveReturnType)
    }

    @Test
    fun testToJSWithDefaults() {
        val command = AddAttributionControl()
        val expectedJS =
            "${MTBridge.MAP_OBJECT}.addControl(new ${MTBridge.SDK_OBJECT}.AttributionControl({}), \"bottom-right\");"
        assertEquals(expectedJS, command.toJS())
    }

    @Test
    fun testToJSWithAllParams() {
        val command =
            AddAttributionControl(
                compact = true,
                customAttribution = "Custom Text",
                position = MTMapCorner.TOP_LEFT,
            )
        val expectedJS =
            "${MTBridge.MAP_OBJECT}.addControl(" +
                "new ${MTBridge.SDK_OBJECT}.AttributionControl({\"compact\":true,\"customAttribution\":\"Custom Text\"}), " +
                "\"top-left\");"
        assertEquals(expectedJS, command.toJS())
    }
}
