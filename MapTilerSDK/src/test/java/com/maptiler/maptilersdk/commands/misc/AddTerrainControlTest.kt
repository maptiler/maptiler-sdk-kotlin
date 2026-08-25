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
            )
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "${MTBridge.MAP_OBJECT}.addControl(" +
                "new ${MTBridge.SDK_OBJECT}.MaptilerTerrainControl(), \"bottom-left\");",
            command.toJS(),
        )
    }

    @Test
    fun testToJS_default() {
        val command = AddTerrainControl()
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "${MTBridge.MAP_OBJECT}.addControl(" +
                "new ${MTBridge.SDK_OBJECT}.MaptilerTerrainControl(), \"top-right\");",
            command.toJS(),
        )
    }
}
