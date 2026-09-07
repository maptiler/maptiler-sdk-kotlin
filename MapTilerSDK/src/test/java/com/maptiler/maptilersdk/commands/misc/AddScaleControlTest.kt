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
        val command = AddScaleControl()
        assertFalse(command.isPrimitiveReturnType)
    }

    @Test
    fun testToJSWithDefaults() {
        val command = AddScaleControl()
        val expectedJS =
            "${MTBridge.MAP_OBJECT}.addControl(new ${MTBridge.SDK_OBJECT}.ScaleControl({}), \"bottom-left\");"
        assertEquals(expectedJS, command.toJS())
    }

    @Test
    fun testToJSWithAllParams() {
        val command =
            AddScaleControl(
                maxWidth = 150,
                unit = MTScaleUnit.IMPERIAL,
                position = MTMapCorner.TOP_RIGHT,
            )
        val expectedJS =
            "${MTBridge.MAP_OBJECT}.addControl(" +
                "new ${MTBridge.SDK_OBJECT}.ScaleControl({\"maxWidth\":150,\"unit\":\"imperial\"}), " +
                "\"top-right\");"
        assertEquals(expectedJS, command.toJS())
    }
}
