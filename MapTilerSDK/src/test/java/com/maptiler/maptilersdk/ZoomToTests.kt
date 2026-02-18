/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.navigation.ZoomTo
import com.maptiler.maptilersdk.map.options.MTAnimationOptions
import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class ZoomToTests {
    @Test
    fun zoomTo_withZoomOnly_returnsCorrectJS() {
        val command = ZoomTo(10.0)
        assertEquals("${MTBridge.MAP_OBJECT}.zoomTo(10.0);", command.toJS())
    }

    @Test
    fun zoomTo_withOptions_returnsCorrectJS() {
        val options = MTAnimationOptions(duration = 1000.0, animate = true)
        val command = ZoomTo(10.0, options)
        // JSON encoding might vary in whitespace/order, but usually predictable with default Json config
        // Properties order: duration, animate, essential, offset
        val expectedOptions = "{\"duration\":1000.0,\"animate\":true}"
        assertEquals("${MTBridge.MAP_OBJECT}.zoomTo(10.0, $expectedOptions);", command.toJS())
    }

    @Test
    fun zoomTo_withAllOptions_returnsCorrectJS() {
        val options =
            MTAnimationOptions(
                duration = 500.0,
                animate = false,
                essential = true,
                offset = MTPoint(10.0, 20.0),
            )
        val command = ZoomTo(5.0, options)
        // MTPoint serializes to {"x":10.0,"y":20.0}
        val expectedOptions = "{\"duration\":500.0,\"animate\":false,\"essential\":true,\"offset\":{\"x\":10.0,\"y\":20.0}}"
        assertEquals("${MTBridge.MAP_OBJECT}.zoomTo(5.0, $expectedOptions);", command.toJS())
    }
}
