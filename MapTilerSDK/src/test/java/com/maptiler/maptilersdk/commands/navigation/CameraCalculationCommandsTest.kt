/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.options.MTFitBoundsOptions
import com.maptiler.maptilersdk.map.types.MTBounds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraCalculationCommandsTest {
    @Test
    fun `CameraForBounds emits expected invocation`() {
        val bounds = MTBounds(-73.9876, 40.7661, -73.9397, 40.8002)
        val options = MTFitBoundsOptions(maxZoom = 14.0)
        val command = CameraForBounds(bounds, options)

        assertEquals(
            "JSON.stringify(map.cameraForBounds([[-73.9876,40.7661],[-73.9397,40.8002]], {\"maxZoom\":14.0}));",
            command.toJS(),
        )
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `CameraForBounds emits expected invocation without options`() {
        val bounds = MTBounds(-73.9876, 40.7661, -73.9397, 40.8002)
        val command = CameraForBounds(bounds)

        assertEquals(
            "JSON.stringify(map.cameraForBounds([[-73.9876,40.7661],[-73.9397,40.8002]], null));",
            command.toJS(),
        )
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `CalculateCameraOptionsFromTo emits expected invocation`() {
        val from = LngLat(0.0, 0.0)
        val to = LngLat(1.0, 1.0)
        val command = CalculateCameraOptionsFromTo(from, 100.0, to, 200.0)

        assertEquals(
            "JSON.stringify(map.calculateCameraOptionsFromTo({\"lng\":0.0,\"lat\":0.0}, 100.0, {\"lng\":1.0,\"lat\":1.0}, 200.0));",
            command.toJS(),
        )
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `CalculateCameraOptionsFromCameraLngLatAltRotation emits expected invocation`() {
        val lngLat = LngLat(0.0, 0.0)
        val command = CalculateCameraOptionsFromCameraLngLatAltRotation(lngLat, 100.0, 90.0, 45.0, 0.0)

        assertEquals(
            "JSON.stringify(map.calculateCameraOptionsFromCameraLngLatAltRotation({\"lng\":0.0,\"lat\":0.0}, 100.0, 90.0, 45.0, 0.0));",
            command.toJS(),
        )
        assertTrue(command.isPrimitiveReturnType)
    }
}
