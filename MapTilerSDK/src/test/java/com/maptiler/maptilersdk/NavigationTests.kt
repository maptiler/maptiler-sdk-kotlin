/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.navigation.GetZoom
import com.maptiler.maptilersdk.commands.navigation.PanBy
import com.maptiler.maptilersdk.commands.navigation.PanTo
import com.maptiler.maptilersdk.commands.navigation.Project
import com.maptiler.maptilersdk.commands.navigation.SetMaxZoom
import com.maptiler.maptilersdk.commands.navigation.SetMinZoom
import com.maptiler.maptilersdk.commands.navigation.SetZoom
import com.maptiler.maptilersdk.commands.navigation.ZoomIn
import com.maptiler.maptilersdk.commands.navigation.ZoomOut
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.types.MTPoint
import junit.framework.TestCase.assertEquals
import org.junit.Test

class NavigationTests {
    @Test fun getZoomToJS_ReturnsCorrectJSString() {
        val command = GetZoom()
        assertEquals("${MTBridge.MAP_OBJECT}.getZoom();", command.toJS())
    }

    @Test fun zoomInToJS_ReturnsCorrectJSString() {
        val command = ZoomIn()
        assertEquals("${MTBridge.MAP_OBJECT}.zoomIn();", command.toJS())
    }

    @Test fun zoomOutToJS_ReturnsCorrectJSString() {
        val command = ZoomOut()
        assertEquals("${MTBridge.MAP_OBJECT}.zoomOut();", command.toJS())
    }

    @Test fun setZoomToJS_ReturnsCorrectJSString() {
        val command = SetZoom(1.0)
        assertEquals("${MTBridge.MAP_OBJECT}.setZoom(1.0);", command.toJS())
    }

    @Test fun setMaxZoomToJS_ReturnsCorrectJSString() {
        val command = SetMaxZoom(1.0)
        assertEquals("${MTBridge.MAP_OBJECT}.setMaxZoom(1.0);", command.toJS())
    }

    @Test fun setMinZoomToJS_ReturnsCorrectJSString() {
        val command = SetMinZoom(1.0)
        assertEquals("${MTBridge.MAP_OBJECT}.setMinZoom(1.0);", command.toJS())
    }

    @Test fun panByToJS_ReturnsCorrectJSString() {
        val offset = MTPoint(1.0, 1.0)
        val command = PanBy(offset)
        assertEquals(
            "${MTBridge.MAP_OBJECT}.panBy({\n" +
                "    \"x\": 1.0,\n" +
                "    \"y\": 1.0\n" +
                "});",
            command.toJS(),
        )
    }

    @Test fun panToToJS_ReturnsCorrectJSString() {
        val coordinates = LngLat(1.0, 1.0)
        val command = PanTo(coordinates)
        assertEquals(
            "${MTBridge.MAP_OBJECT}.panTo({\n" +
                "    \"lng\": 1.0,\n" +
                "    \"lat\": 1.0\n" +
                "});",
            command.toJS(),
        )
    }

    @Test fun projectToJS_ReturnsCorrectJSString() {
        val cmd = Project(LngLat(14.42, 50.09))
        assertEquals("${MTBridge.MAP_OBJECT}.project([14.42, 50.09]);", cmd.toJS())
    }
}
