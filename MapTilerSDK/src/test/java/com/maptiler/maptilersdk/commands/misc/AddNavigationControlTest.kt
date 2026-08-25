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

class AddNavigationControlTest {
    @Test
    fun testToJS() {
        val command =
            AddNavigationControl(
                showCompass = true,
                showZoom = false,
                visualizePitch = true,
                position = MTMapCorner.BOTTOM_LEFT,
            )
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "${MTBridge.MAP_OBJECT}.addControl(" +
                "new ${MTBridge.SDK_OBJECT}.MaptilerNavigationControl(" +
                "{\"showCompass\":true,\"showZoom\":false,\"visualizePitch\":true}), \"bottom-left\");",
            command.toJS(),
        )
    }

    @Test
    fun testToJS_default() {
        val command = AddNavigationControl()
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "${MTBridge.MAP_OBJECT}.addControl(" +
                "new ${MTBridge.SDK_OBJECT}.MaptilerNavigationControl(" +
                "{\"showCompass\":true,\"showZoom\":true,\"visualizePitch\":false}), \"top-right\");",
            command.toJS(),
        )
    }
}
