/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.annotations.MTAnchor
import com.maptiler.maptilersdk.annotations.MTMarker
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
}
