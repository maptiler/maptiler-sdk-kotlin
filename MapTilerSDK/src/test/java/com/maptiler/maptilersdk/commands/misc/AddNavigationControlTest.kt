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
                id = "nav1",
            )
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            """
            if (!window.mapControls) window.mapControls = {};
            window.mapControls['nav1'] = new ${MTBridge.SDK_OBJECT}.MaptilerNavigationControl({"showCompass":true,"showZoom":false,"visualizePitch":true});
            ${MTBridge.MAP_OBJECT}.addControl(window.mapControls['nav1'], "bottom-left");
            """.trimIndent(),
            command.toJS(),
        )
    }

    @Test
    fun testToJS_default() {
        val command = AddNavigationControl(id = "nav1")
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            """
            if (!window.mapControls) window.mapControls = {};
            window.mapControls['nav1'] = new ${MTBridge.SDK_OBJECT}.MaptilerNavigationControl({"showCompass":true,"showZoom":true,"visualizePitch":false});
            ${MTBridge.MAP_OBJECT}.addControl(window.mapControls['nav1'], "top-right");
            """.trimIndent(),
            command.toJS(),
        )
    }
}
