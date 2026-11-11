/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.map.style.layer.symbol.MTSymbolLayer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class BatchLayerCommandsTest {
    private val layers = listOf(MTSymbolLayer("sym1", "src1"), MTSymbolLayer("sym2", "src2"))

    @Test
    fun addLayersToJS_AddsEveryLayer() {
        val command = AddLayers(layers)

        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "${MTBridge.MAP_OBJECT}.addLayer({\"id\":\"sym1\",\"type\":\"symbol\",\"source\":\"src1\"});\n" +
                "${MTBridge.MAP_OBJECT}.addLayer({\"id\":\"sym2\",\"type\":\"symbol\",\"source\":\"src2\"});",
            command.toJS(),
        )
    }

    @Test
    fun addLayersToJS_EmptyListReturnsEmptyString() {
        assertEquals("", AddLayers(emptyList()).toJS())
    }

    @Test
    fun removeLayersToJS_RemovesEveryLayer() {
        val command = RemoveLayers(layers)

        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "${MTBridge.MAP_OBJECT}.removeLayer('sym1');\n${MTBridge.MAP_OBJECT}.removeLayer('sym2');",
            command.toJS(),
        )
    }
}
