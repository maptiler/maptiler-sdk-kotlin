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
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sinh

/**
 * Verifies that the interval-based coverage selects exactly the tiles of the set-based reference
 * implementation in [MTTileMath], which the planner used before.
 */
class MTTileCoverageTest {
    @Test
    fun testRandomBoundingBoxes() {
        val random = SeededRandom(1)
        repeat(300) {
            val zoom = random.int(0..14)
            val span = random.double(0.001, min(40.0, 2000.0 / 2.0.pow(zoom)))
            val minLon = random.double(-180.0, 180.0 - span)
            val minLat = random.double(-80.0, 80.0 - span)
            val bbox = MTBoundingBox(minLon, minLat, minLon + span, minLat + span)
            val padding = random.optionalPadding()

            val coverage = MTTileCoverage.make(MTOfflineRegionGeometry.BoundingBox(bbox), zoom, padding)
            val reference = referenceBoundingBoxTiles(bbox, zoom, padding)
            assertEquals("bbox $bbox z$zoom padding $padding", reference, coverage.tileIndices())
            assertEquals(reference.size.toLong(), coverage.tileCount)
        }
    }

    @Test
    fun testAntimeridianDeduplication() {
        val bbox = MTBoundingBox(170.0, -20.0, -170.0, -10.0)
        for (zoom in 0..6) {
            val coverage = MTTileCoverage.make(MTOfflineRegionGeometry.BoundingBox(bbox), zoom, null)
            val reference = referenceBoundingBoxTiles(bbox, zoom, null)
            assertEquals(reference, coverage.tileIndices())
            assertEquals(reference.size.toLong(), coverage.tileCount)
        }
        // At zoom 0 both halves cover the single world tile.
        assertEquals(1L, MTTileCoverage.make(MTOfflineRegionGeometry.BoundingBox(bbox), 0, null).tileCount)
    }

    @Test
    fun testRandomPolygons() {
        val random = SeededRandom(2)
        for (iteration in 0 until 300) {
            val zoom = random.int(0..13)
            val radius = random.double(0.002, min(15.0, 600.0 / 2.0.pow(zoom)))
            val centerLat = random.double(-70.0, 70.0)
            val centerLng = random.double(-160.0, 160.0)
            val vertexCount = random.int(3..12)
            // Every third polygon has vertices in random order, which makes it self-intersecting.
            var angles = List(vertexCount) { random.double(0.0, 2 * PI) }
            if (iteration % 3 != 0) angles = angles.sorted()
            val coordinates =
                angles
                    .map { angle ->
                        val distance = radius * random.double(0.3, 1.0)
                        LngLat(centerLng + distance * cos(angle), centerLat + distance * sin(angle))
                    }.toMutableList()
            if (iteration % 2 == 0) coordinates.add(coordinates.first())
            val geometry = MTOfflineRegionGeometry.Polygon(coordinates)
            val padding = random.optionalPadding()

            val coverage = MTTileCoverage.make(geometry, zoom, padding)
            val reference = MTTileMath.tiles(geometry, zoom, padding)
            assertEquals("polygon $coordinates z$zoom", reference, coverage.tileIndices())
            assertEquals(reference.size.toLong(), coverage.tileCount)
        }
    }

    @Test
    fun testPolygonOnTileGrid() {
        // Vertices placed exactly on tile center coordinates exercise the strict comparisons of the point test.
        for (zoom in 3..8) {
            val n = 2.0.pow(zoom)
            val lon = { x: Double -> (x + 0.5) / n * 360.0 - 180.0 }
            val lat = { y: Double -> atan(sinh(PI * (1.0 - 2.0 * (y + 0.5) / n))) * 180.0 / PI }
            val base = n / 2
            val geometry =
                MTOfflineRegionGeometry.Polygon(
                    listOf(
                        LngLat(lon(base - 2), lat(base - 2)),
                        LngLat(lon(base + 2), lat(base - 2)),
                        LngLat(lon(base + 2), lat(base + 2)),
                        LngLat(lon(base), lat(base)),
                        LngLat(lon(base - 2), lat(base + 2)),
                    ),
                )
            val coverage = MTTileCoverage.make(geometry, zoom, 0.0)
            assertEquals(MTTileMath.tiles(geometry, zoom, 0.0), coverage.tileIndices())
        }
    }

    @Test
    fun testRandomRoutes() {
        val random = SeededRandom(3)
        repeat(300) {
            val zoom = random.int(0..15)
            val step = random.double(0.0005, min(10.0, 300.0 / 2.0.pow(zoom)))
            var lat = random.double(-70.0, 70.0)
            var lng = random.double(-160.0, 160.0)
            val coordinates = mutableListOf(LngLat(lng, lat))
            repeat(random.int(0..10)) {
                lat = (lat + random.double(-step, step)).coerceIn(-80.0, 80.0)
                lng = (lng + random.double(-step, step)).coerceIn(-179.0, 179.0)
                coordinates.add(LngLat(lng, lat))
            }
            val geometry = MTOfflineRegionGeometry.Route(coordinates)
            val padding = random.optionalPadding()

            val coverage = MTTileCoverage.make(geometry, zoom, padding)
            val reference = MTTileMath.tiles(geometry, zoom, padding)
            assertEquals("route $coordinates z$zoom", reference, coverage.tileIndices())
            assertEquals(reference.size.toLong(), coverage.tileCount)
        }
    }

    @Test
    fun testCountsByAncestor() {
        val random = SeededRandom(4)
        repeat(100) {
            val zoom = random.int(0..13)
            val ancestorZoom = random.int(0..zoom)
            val span = random.double(0.01, min(30.0, 1500.0 / 2.0.pow(zoom)))
            val minLon = random.double(-180.0, 180.0 - span)
            val minLat = random.double(-80.0, 80.0 - span)
            val geometry =
                if (random.int(0..1) == 0) {
                    MTOfflineRegionGeometry.BoundingBox(MTBoundingBox(minLon, minLat, minLon + span, minLat + span))
                } else {
                    MTOfflineRegionGeometry.Route(listOf(LngLat(minLon, minLat), LngLat(minLon + span / 2, minLat + span)))
                }
            val coverage = MTTileCoverage.make(geometry, zoom, random.optionalPadding())

            val expected = mutableMapOf<MTTileIndex, Long>()
            val shift = zoom - ancestorZoom
            coverage.forEachTile { x, y ->
                val key = MTTileIndex(x shr shift, y shr shift, ancestorZoom)
                expected[key] = (expected[key] ?: 0L) + 1
            }
            assertEquals(expected, coverage.tileCountsByAncestor(ancestorZoom))
        }
    }

    @Test
    fun testLargeRegionCount() {
        val world = MTBoundingBox(-180.0, -85.0, 180.0, 85.0)
        val coverage = MTTileCoverage.make(MTOfflineRegionGeometry.BoundingBox(world), 22, null)
        val bounds = MTTileMath.tileBounds(world, 22, 1)
        assertEquals(MTTileMath.safeMaxTile(22), bounds.maxX)
        assertEquals((bounds.maxX - bounds.minX + 1).toLong() * (bounds.maxY - bounds.minY + 1), coverage.tileCount)

        val switzerland =
            MTOfflineRegionGeometry.Polygon(
                listOf(LngLat(6.02, 46.14), LngLat(9.04, 45.84), LngLat(10.47, 46.55), LngLat(8.57, 47.8)),
            )
        assertTrue(MTTileCoverage.make(switzerland, 18, 2000.0).tileCount > 1_000_000)
    }

    /**
     * The tiles the planner downloaded for a bounding box before coverage was introduced.
     */
    private fun referenceBoundingBoxTiles(
        bbox: MTBoundingBox,
        zoom: Int,
        paddingMeters: Double?,
    ): Set<MTTileIndex> {
        val tiles = mutableSetOf<MTTileIndex>()
        for (box in bbox.normalizedAndSplit()) {
            val (xs, ys) = MTTileMath.tileRanges(box, zoom, paddingMeters)
            for (x in xs) for (y in ys) tiles.add(MTTileIndex(x, y, zoom))
        }
        return tiles
    }
}

/**
 * A small deterministic random generator (SplitMix64) so failures are reproducible.
 */
class SeededRandom(
    private var state: Long,
) {
    fun next(): Long {
        state += -0x61c8864680b583ebL
        var z = state
        z = (z xor (z ushr 30)) * -0x40a7b892e31b1a47L
        z = (z xor (z ushr 27)) * -0x6b2fb644ecceee15L
        return z xor (z ushr 31)
    }

    fun double(
        from: Double,
        to: Double,
    ): Double {
        val unit = (next() ushr 11).toDouble() / (1L shl 53).toDouble()
        return from + unit * (to - from)
    }

    fun int(range: IntRange): Int = range.first + java.lang.Long.remainderUnsigned(next(), range.count().toLong()).toInt()

    fun optionalPadding(): Double? =
        when (int(0..3)) {
            0 -> null
            1 -> 0.0
            else -> double(1.0, 5000.0)
        }
}
