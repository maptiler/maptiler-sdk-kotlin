/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapCameraHelper
import junit.framework.TestCase.assertEquals
import org.junit.Test

class MTMapCameraHelperTests {
    @Test fun getCamera_ReturnsAllZeroProperties() {
        val camera = MTMapCameraHelper.getCamera()

        assertEquals(0.0, camera.centerCoordinate?.lng)
        assertEquals(0.0, camera.centerCoordinate?.lat)
        assertEquals(0.0, camera.bearing)
        assertEquals(0.0, camera.pitch)
        assertEquals(0.0, camera.roll)
        assertEquals(0.0, camera.elevation)
    }

    @Test fun cameraLookingAtCenterCoordinate_SetsCorrectly() {
        val center = LngLat(10.0, 20.0)
        val camera =
            MTMapCameraHelper.cameraLookingAtCenterCoordinate(
                centerCoordinate = center,
                bearing = 1.0,
                pitch = 2.0,
                roll = 3.0,
                elevation = 4.0,
            )

        assertEquals(center, camera.centerCoordinate)
        assertEquals(1.0, camera.bearing)
        assertEquals(2.0, camera.pitch)
        assertEquals(3.0, camera.roll)
        assertEquals(4.0, camera.elevation)
    }
}
