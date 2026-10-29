/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.helpers.MTMath
import com.maptiler.maptilersdk.map.LngLat
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sinh

/**
 * A horizontal band of tile rows that share the same set of covered columns.
 */
internal data class MTTileBand(
    /** The inclusive range of tile rows (Y, XYZ scheme) covered by the band. */
    val rows: IntRange,
    /** Sorted, non-overlapping, non-adjacent inclusive ranges of tile columns (X). */
    val columns: List<IntRange>,
) {
    val tileCount: Long
        get() = rows.count().toLong() * columns.sumOf { it.count().toLong() }
}

/**
 * The set of tiles covering a geometry at a single zoom level.
 *
 * Tiles are stored as row bands of column intervals rather than as individual tiles, so very large
 * regions can be counted without materializing every tile. The covered tiles are identical to the ones
 * produced by the set-based helpers in [MTTileMath], which remain the reference implementation.
 */
internal data class MTTileCoverage(
    val zoom: Int,
    /** Bands sorted by row, never overlapping each other. */
    val bands: List<MTTileBand>,
) {
    val tileCount: Long
        get() = bands.sumOf { it.tileCount }

    /**
     * Calls [action] for every covered tile, ordered by row and then by column.
     */
    inline fun forEachTile(action: (x: Int, y: Int) -> Unit) {
        for (band in bands) {
            for (y in band.rows) {
                for (columns in band.columns) {
                    for (x in columns) {
                        action(x, y)
                    }
                }
            }
        }
    }

    /**
     * Counts covered tiles per ancestor tile at [ancestorZoom] (which must not exceed [zoom]),
     * splitting bands and column ranges at ancestor boundaries instead of visiting each tile.
     */
    fun tileCountsByAncestor(ancestorZoom: Int): Map<MTTileIndex, Long> {
        val shift = zoom - ancestorZoom
        val size = 1 shl shift
        val counts = mutableMapOf<MTTileIndex, Long>()

        for (band in bands) {
            var rowStart = band.rows.first
            while (rowStart <= band.rows.last) {
                val ancestorY = rowStart shr shift
                val rowEnd = minOf(band.rows.last, (ancestorY + 1) * size - 1)
                val height = (rowEnd - rowStart + 1).toLong()

                for (columns in band.columns) {
                    var columnStart = columns.first
                    while (columnStart <= columns.last) {
                        val ancestorX = columnStart shr shift
                        val columnEnd = minOf(columns.last, (ancestorX + 1) * size - 1)
                        val ancestor = MTTileIndex(ancestorX, ancestorY, ancestorZoom)
                        counts[ancestor] = (counts[ancestor] ?: 0L) + height * (columnEnd - columnStart + 1)
                        columnStart = columnEnd + 1
                    }
                }
                rowStart = rowEnd + 1
            }
        }
        return counts
    }

    /**
     * Returns all covered tiles. Intended for tests and small regions only.
     */
    fun tileIndices(): Set<MTTileIndex> {
        val result = mutableSetOf<MTTileIndex>()
        forEachTile { x, y -> result.add(MTTileIndex(x, y, zoom)) }
        return result
    }

    companion object {
        /**
         * Computes the tiles the offline planner downloads for a geometry at one zoom level.
         *
         * Bounding boxes are normalized and split at the antimeridian, each part padded by its own buffer,
         * and overlapping parts are merged so that no tile is counted twice. Routes and polygons are padded by
         * a tile buffer derived from [paddingMeters] and the geometry's bounding box.
         */
        fun make(
            geometry: MTOfflineRegionGeometry,
            zoom: Int,
            paddingMeters: Double?,
        ): MTTileCoverage =
            when (geometry) {
                is MTOfflineRegionGeometry.BoundingBox -> {
                    val rectangles =
                        geometry.bbox.normalizedAndSplit().map { box ->
                            val buffer = MTTileMath.calculateTileBuffer(paddingMeters, box, zoom)
                            MTTileMath.tileBounds(box, zoom, buffer)
                        }
                    union(rectangles, zoom)
                }
                is MTOfflineRegionGeometry.Route -> {
                    val buffer = MTTileMath.calculateTileBuffer(paddingMeters, geometry.bbox, zoom)
                    fromRows(zoom, routeRows(geometry.coordinates, zoom), buffer)
                }
                is MTOfflineRegionGeometry.Polygon -> {
                    val buffer = MTTileMath.calculateTileBuffer(paddingMeters, geometry.bbox, zoom)
                    val coordinates = geometry.coordinates
                    val rows = if (coordinates.size > 2) polygonRows(coordinates, zoom) else routeRows(coordinates, zoom)
                    fromRows(zoom, rows, buffer)
                }
            }

        /**
         * Merges possibly overlapping tile rectangles into disjoint bands.
         */
        fun union(
            rectangles: List<MTTileBounds>,
            zoom: Int,
        ): MTTileCoverage {
            val boundaries = rectangles.flatMap { listOf(it.minY, it.maxY + 1) }.toSortedSet().toList()
            val bands = mutableListOf<MTTileBand>()

            for (index in 0 until boundaries.size - 1) {
                val rows = boundaries[index]..<boundaries[index + 1]
                val columns =
                    rectangles
                        .filter { it.minY <= rows.first && it.maxY >= rows.last }
                        .map { it.minX..it.maxX }
                val merged = mergeRanges(columns)
                if (merged.isNotEmpty()) {
                    bands.add(MTTileBand(rows, merged))
                }
            }
            return MTTileCoverage(zoom, bands)
        }

        /**
         * Builds a coverage from per-row column ranges, dilating it by [buffer] tiles in every direction
         * (clamped to the valid tile range), which matches [MTTileMath.applyBuffer].
         */
        fun fromRows(
            zoom: Int,
            rows: Map<Int, List<IntRange>>,
            buffer: Int,
        ): MTTileCoverage {
            val maxIndex = MTTileMath.safeMaxTile(zoom)
            val dilated: Map<Int, List<IntRange>> =
                if (buffer > 0) {
                    val result = mutableMapOf<Int, MutableList<IntRange>>()
                    for ((y, columns) in rows) {
                        val expanded =
                            columns.map {
                                (it.first - buffer).coerceAtLeast(0)..(it.last + buffer).coerceAtMost(maxIndex)
                            }
                        for (row in (y - buffer).coerceAtLeast(0)..(y + buffer).coerceAtMost(maxIndex)) {
                            result.getOrPut(row) { mutableListOf() }.addAll(expanded)
                        }
                    }
                    result
                } else {
                    rows
                }

            val bands =
                dilated.keys.sorted().mapNotNull { y ->
                    val merged = mergeRanges(dilated[y].orEmpty())
                    if (merged.isEmpty()) null else MTTileBand(y..y, merged)
                }
            return MTTileCoverage(zoom, bands)
        }

        /**
         * Sorts ranges and merges the ones that overlap or touch.
         */
        fun mergeRanges(ranges: List<IntRange>): List<IntRange> {
            val merged = mutableListOf<IntRange>()
            for (range in ranges.sortedBy { it.first }) {
                val last = merged.lastOrNull()
                if (last != null && range.first <= last.last + 1) {
                    merged[merged.size - 1] = last.first..maxOf(last.last, range.last)
                } else {
                    merged.add(range)
                }
            }
            return merged
        }

        /**
         * Tiles touched by a route before buffering, grouped by row. Mirrors [MTTileMath.tilesForRoute].
         */
        fun routeRows(
            route: List<LngLat>,
            zoom: Int,
        ): Map<Int, List<IntRange>> {
            if (route.isEmpty()) return emptyMap()

            if (route.size == 1) {
                val x = MTTileMath.longitudeToTileX(route[0].lng, zoom)
                val y = MTTileMath.latitudeToTileY(route[0].lat, zoom)
                return mapOf(y to listOf(x..x))
            }

            val rows = mutableMapOf<Int, MutableList<IntRange>>()
            val zoomDouble = zoom.coerceIn(0, 62).toDouble()
            for (i in 0 until route.size - 1) {
                val p1 = route[i]
                val p2 = route[i + 1]

                val x1 = MTMath.longitudeToTileX(p1.lng, zoomDouble, false)
                val y1 = MTMath.latitudeToTileY(p1.lat, zoomDouble, false)
                val x2 = MTMath.longitudeToTileX(p2.lng, zoomDouble, false)
                val y2 = MTMath.latitudeToTileY(p2.lat, zoomDouble, false)

                for (tile in MTTileMath.tilesIntersectingSegment(x1, y1, x2, y2, zoom)) {
                    rows.getOrPut(tile.y) { mutableListOf() }.add(tile.x..tile.x)
                }
            }
            return rows
        }

        /**
         * Tiles covered by a polygon before buffering, grouped by row. Mirrors [MTTileMath.tilesForPolygon]:
         * the tiles crossed by the outline plus every tile in the polygon's bounds whose center lies inside it.
         *
         * Instead of testing each tile center, each row computes where the outline crosses the row's center
         * latitude and derives the inside column ranges from those crossings, using the same arithmetic as the
         * point test.
         */
        fun polygonRows(
            polygon: List<LngLat>,
            zoom: Int,
        ): Map<Int, List<IntRange>> {
            val closedPolygon = polygon.toMutableList()
            if (polygon.first() != polygon.last()) {
                closedPolygon.add(polygon.first())
            }

            val rows = routeRows(closedPolygon, zoom).mapValues { it.value.toMutableList() }.toMutableMap()

            val bounds = MTTileMath.tileBounds(MTBoundingBox.fromCoordinates(polygon), zoom, 0)
            val columns = bounds.minX..bounds.maxX
            val n = 2.0.pow(zoom.toDouble())
            val centerLongitude = { x: Int -> (x.toDouble() + 0.5) / n * 360.0 - 180.0 }

            for (y in bounds.minY..bounds.maxY) {
                val latRad = atan(sinh(PI * (1.0 - 2.0 * (y.toDouble() + 0.5) / n)))
                val lat = latRad * 180.0 / PI

                val crossings = mutableListOf<Double>()
                var j = closedPolygon.size - 1
                for (i in closedPolygon.indices) {
                    val pi = closedPolygon[i]
                    val pj = closedPolygon[j]
                    if ((pi.lat > lat) != (pj.lat > lat)) {
                        crossings.add((pj.lng - pi.lng) * (lat - pi.lat) / (pj.lat - pi.lat) + pi.lng)
                    }
                    j = i
                }
                crossings.sort()

                // A center longitude `c` is inside when an odd number of crossings lie strictly east of it.
                // Between crossings[t - 1] (inclusive) and crossings[t] (exclusive) that number is `count - t`.
                val count = crossings.size
                for (t in 0..count) {
                    if ((count - t) % 2 != 1) continue
                    val lower = if (t == 0) Double.NEGATIVE_INFINITY else crossings[t - 1]
                    val upper = if (t == count) Double.POSITIVE_INFINITY else crossings[t]
                    val firstX = firstIndex(columns) { centerLongitude(it) >= lower } ?: continue
                    val endX = firstIndex(columns) { centerLongitude(it) >= upper } ?: (columns.last + 1)
                    if (firstX < endX) {
                        rows.getOrPut(y) { mutableListOf() }.add(firstX..<endX)
                    }
                }
            }
            return rows
        }

        /**
         * Binary search for the first index in [range] satisfying a predicate that is monotonic over the range.
         */
        private inline fun firstIndex(
            range: IntRange,
            predicate: (Int) -> Boolean,
        ): Int? {
            var low = range.first
            var high = range.last + 1
            while (low < high) {
                val mid = low + (high - low) / 2
                if (predicate(mid)) {
                    high = mid
                } else {
                    low = mid + 1
                }
            }
            return if (low <= range.last) low else null
        }
    }
}
