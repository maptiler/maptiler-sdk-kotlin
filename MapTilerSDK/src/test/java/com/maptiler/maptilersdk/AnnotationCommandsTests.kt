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
import com.maptiler.maptilersdk.commands.annotations.GetMarkerLngLat
import com.maptiler.maptilersdk.commands.annotations.GetMarkerOffset
import com.maptiler.maptilersdk.commands.annotations.GetMarkerPitchAlignment
import com.maptiler.maptilersdk.commands.annotations.GetMarkerRotation
import com.maptiler.maptilersdk.commands.annotations.GetMarkerRotationAlignment
import com.maptiler.maptilersdk.commands.annotations.IsMarkerDraggable
import com.maptiler.maptilersdk.map.LngLat
import org.junit.Assert.assertEquals
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

    @Test fun addMarkerToJS_IncludesScaleAndSubpixelPositioningOptions() {
        val marker = MTMarker(LngLat(10.0, 20.0))
        marker.scale = 2.0
        marker.subpixelPositioning = false

        val js = AddMarker(marker).toJS()

        assertTrue(js.contains("scale: 2.0"))
        assertTrue(js.contains("subpixelPositioning: false"))
    }

    @Test fun markerGetterCommands_ToJS_UseMarkerIdentifier() {
        val marker = MTMarker(LngLat(10.0, 20.0))

        assertEquals("${marker.identifier}.getLngLat();", GetMarkerLngLat(marker).toJS())
        assertEquals("${marker.identifier}.getPitchAlignment();", GetMarkerPitchAlignment(marker).toJS())
        assertEquals("${marker.identifier}.getRotation();", GetMarkerRotation(marker).toJS())
        assertEquals("${marker.identifier}.getRotationAlignment();", GetMarkerRotationAlignment(marker).toJS())
        assertEquals("${marker.identifier}.getOffset();", GetMarkerOffset(marker).toJS())
        assertEquals("${marker.identifier}.isDraggable();", IsMarkerDraggable(marker).toJS())
    }
}
