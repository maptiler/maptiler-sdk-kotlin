/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.annotations.MTAnchor
import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.annotations.MTPitchAlignment
import com.maptiler.maptilersdk.annotations.MTRotationAlignment
import com.maptiler.maptilersdk.commands.annotations.AddMarker
import com.maptiler.maptilersdk.map.LngLat
import org.junit.Assert.assertTrue
import org.junit.Test

class AnnotationCommandsTests {
    @Test fun addMarkerToJS_IncludesAnchorAndOffsetOptions() {
        val marker = MTMarker(LngLat(10.0, 20.0))
        marker.anchor = MTAnchor.BOTTOM_LEFT
        marker.offset = 12.5

        val js = AddMarker(marker).toJS()

        assertTrue(js.contains("anchor: 'bottom-left'"))
        assertTrue(js.contains("offset: 12.5"))
    }

    @Test fun addMarkerToJS_IncludesOpacityOptions() {
        val marker = MTMarker(LngLat(10.0, 20.0))
        marker.opacity = 0.5
        marker.opacityWhenCovered = 0.25

        val js = AddMarker(marker).toJS()

        assertTrue(js.contains("opacity: 0.5"))
        assertTrue(js.contains("opacityWhenCovered: 0.25"))
    }

    @Test fun addMarkerToJS_IncludesAlignmentAndRotationOptions() {
        val marker = MTMarker(LngLat(10.0, 20.0))
        marker.pitchAlignment = MTPitchAlignment.MAP
        marker.rotation = 45.0
        marker.rotationAlignment = MTRotationAlignment.MAP

        val js = AddMarker(marker).toJS()

        assertTrue(js.contains("pitchAlignment: 'map'"))
        assertTrue(js.contains("rotation: 45.0"))
        assertTrue(js.contains("rotationAlignment: 'map'"))
    }
}
