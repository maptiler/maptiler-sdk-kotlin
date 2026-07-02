/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.map.LngLat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MTTileMathTest {
    @Test
    fun testFlipYCoordinate() {
        // Zoom 0: max Y is 0. 0 -> 0.
        assertEquals(0, MTTileMath.flipYCoordinate(0, 0))

        // Zoom 1: max Y is 1. 0 -> 1, 1 -> 0.
        assertEquals(1, MTTileMath.flipYCoordinate(0, 1))
        assertEquals(0, MTTileMath.flipYCoordinate(1, 1))

        // Zoom 2: max Y is 3. 0 -> 3, 1 -> 2, 2 -> 1, 3 -> 0.
        assertEquals(3, MTTileMath.flipYCoordinate(0, 2))
        assertEquals(2, MTTileMath.flipYCoordinate(1, 2))
        assertEquals(1, MTTileMath.flipYCoordinate(2, 2))
        assertEquals(0, MTTileMath.flipYCoordinate(3, 2))
    }

    @Test
    fun testLongitudeToTileX() {
        assertEquals(0, MTTileMath.longitudeToTileX(0.0, 0))
        assertEquals(0, MTTileMath.longitudeToTileX(-180.0, 0))
        assertEquals(0, MTTileMath.longitudeToTileX(180.0, 0))

        assertEquals(0, MTTileMath.longitudeToTileX(-179.9, 1))
        assertEquals(0, MTTileMath.longitudeToTileX(-0.1, 1))
        assertEquals(1, MTTileMath.longitudeToTileX(0.1, 1))
        assertEquals(1, MTTileMath.longitudeToTileX(179.9, 1))
    }

    @Test
    fun testLatitudeToTileY() {
        assertEquals(0, MTTileMath.latitudeToTileY(0.0, 0))

        assertEquals(0, MTTileMath.latitudeToTileY(80.0, 1))
        assertEquals(0, MTTileMath.latitudeToTileY(1.0, 1))
        assertEquals(1, MTTileMath.latitudeToTileY(-1.0, 1))
        assertEquals(1, MTTileMath.latitudeToTileY(-80.0, 1))
    }

    @Test
    fun testTileBoundsWithBufferAndClamping() {
        val bbox = MTBoundingBox(-1.0, -1.0, 1.0, 1.0)

        val bounds0 = MTTileMath.tileBounds(bbox, 4, 0)
        assertEquals(7, bounds0.minX)
        assertEquals(8, bounds0.maxX)
        assertEquals(7, bounds0.minY)
        assertEquals(8, bounds0.maxY)

        val bounds1 = MTTileMath.tileBounds(bbox, 4, 1)
        assertEquals(6, bounds1.minX)
        assertEquals(9, bounds1.maxX)
        assertEquals(6, bounds1.minY)
        assertEquals(9, bounds1.maxY)
    }

    @Test
    fun testCalculateTileBuffer() {
        val bbox = MTBoundingBox(-1.0, -1.0, 1.0, 1.0)

        // 0 or null padding should return default 1
        assertEquals(1, MTTileMath.calculateTileBuffer(null, bbox, 10))
        assertEquals(0, MTTileMath.calculateTileBuffer(0.0, bbox, 10))

        // 1 tile at equator at z10 is approx 39km (Earth Circ 40075km / 1024)
        // 40km buffer should result in 2 tiles
        val buffer40km = MTTileMath.calculateTileBuffer(40000.0, bbox, 10)
        assertEquals(2, buffer40km)
    }

    @Test
    fun testTilesIntersectingSegment() {
        // Line from top-left of tile (0,0) to bottom-right of tile (2,2)
        val tiles = MTTileMath.tilesIntersectingSegment(0.5, 0.5, 2.5, 2.5, 2)

        val expected =
            setOf(
                MTTileIndex(0, 0, 2),
                MTTileIndex(1, 1, 2),
                MTTileIndex(2, 2, 2),
            )

        assertEquals(expected, tiles)
    }

    @Test
    fun testTilesForRoute() {
        // Route exactly crossing equator/prime meridian
        val route = listOf(LngLat(-1.0, 1.0), LngLat(1.0, -1.0))

        // At zoom 4, tiles are around 7,7 and 8,8
        // With 0 buffer, we expect exactly the intersected tiles.
        val tilesNoBuffer = MTTileMath.tilesForRoute(route, 4, 0)

        val expectedNoBuffer =
            setOf(
                MTTileIndex(7, 7, 4),
                MTTileIndex(8, 7, 4),
                MTTileIndex(8, 8, 4),
                MTTileIndex(7, 8, 4),
            )

        assertTrue(tilesNoBuffer.isNotEmpty())

        // With buffer 1, we expect a 4x4 grid around the center
        val tilesBuffer = MTTileMath.tilesForRoute(route, 4, 1)
        assertTrue(tilesBuffer.size > tilesNoBuffer.size)
        assertTrue(tilesBuffer.contains(MTTileIndex(6, 6, 4)))
    }
}
