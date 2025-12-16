/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.annotations.MTPitchAlignment
import com.maptiler.maptilersdk.annotations.MTRotationAlignment
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.bridge.MTCommandExecutable
import com.maptiler.maptilersdk.commands.annotations.GetMarkerLngLat
import com.maptiler.maptilersdk.commands.annotations.GetMarkerOffset
import com.maptiler.maptilersdk.commands.annotations.GetMarkerPitchAlignment
import com.maptiler.maptilersdk.commands.annotations.GetMarkerRotation
import com.maptiler.maptilersdk.commands.annotations.GetMarkerRotationAlignment
import com.maptiler.maptilersdk.commands.annotations.IsMarkerDraggable
import com.maptiler.maptilersdk.map.LngLat
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.ArrayDeque

class MarkerGetterTests {
    @Test fun markerGetters_ParseBridgeResponsesAndUpdateState() =
        runBlocking {
            val executed = mutableListOf<MTCommand>()
            val responses =
                ArrayDeque(
                    listOf(
                        MTBridgeReturnType.StringValue("{\"lng\":5.0,\"lat\":6.0}"),
                        MTBridgeReturnType.StringValue("\"viewport\""),
                        MTBridgeReturnType.StringValue("12.5"),
                        MTBridgeReturnType.StringValue("\"map\""),
                        MTBridgeReturnType.DoubleValue(3.25),
                        MTBridgeReturnType.StringValue("true"),
                    ),
                )

            val exec =
                object : MTCommandExecutable {
                    override suspend fun execute(command: MTCommand): MTBridgeReturnType {
                        executed.add(command)
                        return responses.removeFirst()
                    }
                }

            val marker = MTMarker(LngLat(1.0, 2.0))
            marker.bindBridge(MTBridge(exec))

            val lngLat = marker.getLngLat()
            val pitchAlignment = marker.getPitchAlignment()
            val rotation = marker.getRotation()
            val rotationAlignment = marker.getRotationAlignment()
            val offset = marker.getOffset()
            val draggable = marker.isDraggable()

            assertEquals(LngLat(5.0, 6.0), lngLat)
            assertEquals(LngLat(5.0, 6.0), marker.coordinates)
            assertEquals(MTPitchAlignment.VIEWPORT, pitchAlignment)
            assertEquals(MTPitchAlignment.VIEWPORT, marker.pitchAlignment)
            assertEquals(12.5, rotation, 0.0)
            assertEquals(12.5, marker.rotation, 0.0)
            assertEquals(MTRotationAlignment.MAP, rotationAlignment)
            assertEquals(MTRotationAlignment.MAP, marker.rotationAlignment)
            assertEquals(3.25, offset, 0.0)
            assertEquals(3.25, marker.offset, 0.0)
            assertTrue(draggable)
            assertTrue(marker.draggable == true)

            assertTrue(executed[0] is GetMarkerLngLat)
            assertTrue(executed[1] is GetMarkerPitchAlignment)
            assertTrue(executed[2] is GetMarkerRotation)
            assertTrue(executed[3] is GetMarkerRotationAlignment)
            assertTrue(executed[4] is GetMarkerOffset)
            assertTrue(executed[5] is IsMarkerDraggable)
        }

    @Test fun markerGetters_ReturnLocalValuesWhenBridgeMissing() =
        runBlocking {
            val marker = MTMarker(LngLat(0.0, 0.0))
            marker.pitchAlignment = MTPitchAlignment.MAP
            marker.rotation = 7.5
            marker.rotationAlignment = MTRotationAlignment.VIEWPORT
            marker.offset = 1.25
            marker.draggable = true

            assertEquals(LngLat(0.0, 0.0), marker.getLngLat())
            assertEquals(MTPitchAlignment.MAP, marker.getPitchAlignment())
            assertEquals(7.5, marker.getRotation(), 0.0)
            assertEquals(MTRotationAlignment.VIEWPORT, marker.getRotationAlignment())
            assertEquals(1.25, marker.getOffset(), 0.0)
            assertTrue(marker.isDraggable())
        }
}
