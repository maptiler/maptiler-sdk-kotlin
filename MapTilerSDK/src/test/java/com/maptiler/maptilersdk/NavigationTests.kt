/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.navigation.GetCenterClampedToGround
import com.maptiler.maptilersdk.commands.navigation.GetCenterElevation
import com.maptiler.maptilersdk.commands.navigation.GetMaxPitch
import com.maptiler.maptilersdk.commands.navigation.GetMaxZoom
import com.maptiler.maptilersdk.commands.navigation.GetMinPitch
import com.maptiler.maptilersdk.commands.navigation.GetMinZoom
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
    @Test fun getCenterClampedToGroundToJS_ReturnsCorrectJSString() {
        val command = GetCenterClampedToGround()
        assertEquals("${MTBridge.MAP_OBJECT}.getCenterClampedToGround();", command.toJS())
    }

    @Test fun getCenterElevationToJS_ReturnsCorrectJSString() {
        val command = GetCenterElevation()
        assertEquals("${MTBridge.MAP_OBJECT}.getCenterElevation();", command.toJS())
    }

    @Test fun getMaxZoomToJS_ReturnsCorrectJSString() {
        val command = GetMaxZoom()
        assertEquals("${MTBridge.MAP_OBJECT}.getMaxZoom();", command.toJS())
    }

    @Test fun getMinZoomToJS_ReturnsCorrectJSString() {
        val command = GetMinZoom()
        assertEquals("${MTBridge.MAP_OBJECT}.getMinZoom();", command.toJS())
    }

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

    @Test fun getMaxPitchToJS_ReturnsCorrectJSString() {
        val command = GetMaxPitch()
        assertEquals("${MTBridge.MAP_OBJECT}.getMaxPitch();", command.toJS())
    }

    @Test fun getMinPitchToJS_ReturnsCorrectJSString() {
        val command = GetMinPitch()
        assertEquals("${MTBridge.MAP_OBJECT}.getMinPitch();", command.toJS())
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
        assertEquals("${MTBridge.MAP_OBJECT}.panBy([1.0, 1.0]);", command.toJS())
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
