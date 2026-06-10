/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.helpers.MTMath
import com.maptiler.maptilersdk.map.LngLat
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sinh

/**
 * Represents a specific tile coordinate.
 */
internal data class MTTileIndex(
    val x: Int,
    val y: Int,
    val z: Int,
)

/**
 * Represents a range of tile coordinates.
 */
internal data class MTTileBounds(
    val minX: Int,
    val minY: Int,
    val maxX: Int,
    val maxY: Int,
)

/**
 * Pure math helpers for Web Mercator calculations and offline estimation.
 */
internal object MTTileMath {
    /**
     * Helper to safely calculate the maximum tile index for a given zoom level without overflow.
     */
    fun safeMaxTile(zoom: Int): Int {
        val safeZoom = zoom.coerceIn(0, 62)
        return (1L shl safeZoom).toInt() - 1
    }

    /**
     * Converts an XYZ Y-coordinate to a TMS Y-coordinate, or vice-versa.
     */
    fun flipYCoordinate(
        y: Int,
        zoom: Int,
    ): Int {
        val maxTileY = safeMaxTile(zoom)
        return maxTileY - y
    }

    /**
     * Calculates the Web Mercator tile X coordinate for a given longitude and zoom level.
     */
    fun longitudeToTileX(
        lon: Double,
        zoom: Int,
    ): Int {
        val safeZoom = zoom.coerceIn(0, 62)
        val maxTile = safeMaxTile(safeZoom)
        val x = MTMath.longitudeToTileX(lon, safeZoom.toDouble(), true).toInt()
        return x.coerceIn(0, maxTile)
    }

    /**
     * Calculates the Web Mercator tile Y coordinate (XYZ scheme) for a given latitude and zoom level.
     */
    fun latitudeToTileY(
        lat: Double,
        zoom: Int,
    ): Int {
        val safeZoom = zoom.coerceIn(0, 62)
        val maxTile = safeMaxTile(safeZoom)
        val clampedLat = lat.coerceIn(-MTMath.MAX_SAFE_LATITUDE, MTMath.MAX_SAFE_LATITUDE)
        val y = MTMath.latitudeToTileY(clampedLat, safeZoom.toDouble(), true).toInt()
        return y.coerceIn(0, maxTile)
    }

    /**
     * Calculates the required tile buffer size for a given distance padding in meters at a specific zoom and latitude.
     */
    fun calculateTileBuffer(
        paddingMeters: Double?,
        boundingBox: MTBoundingBox,
        zoom: Int,
    ): Int {
        if (paddingMeters == null) return 1
        if (paddingMeters <= 0) return 0

        val maxAbsLat = maxOf(abs(boundingBox.minLat), abs(boundingBox.maxLat))
        val clampedLat = minOf(maxAbsLat, MTMath.MAX_SAFE_LATITUDE)
        val cosLat = cos(MTMath.toRadians(clampedLat))
        val tilesAtZoom = 2.0.pow(zoom.coerceIn(0, 62).toDouble())
        val metersPerTile = (MTMath.EARTH_CIRCUMFERENCE * cosLat) / tilesAtZoom

        if (metersPerTile <= 0) return 1
        val requiredTiles = ceil(paddingMeters / metersPerTile).toInt()
        return requiredTiles.coerceIn(1, 50)
    }

    /**
     * Calculates the discrete tile bounds intersecting a bounding box at a given zoom level.
     */
    fun tileBounds(
        bbox: MTBoundingBox,
        zoom: Int,
        buffer: Int = 1,
    ): MTTileBounds {
        val minXRaw = longitudeToTileX(bbox.minLon, zoom)
        val maxXRaw = longitudeToTileX(bbox.maxLon, zoom)
        val minYRaw = latitudeToTileY(bbox.maxLat, zoom)
        val maxYRaw = latitudeToTileY(bbox.minLat, zoom)

        val minX = minOf(minXRaw, maxXRaw)
        val minY = minOf(minYRaw, maxYRaw)
        val maxX = maxOf(minXRaw, maxXRaw)
        val maxY = maxOf(minYRaw, maxYRaw)

        val maxIdx = safeMaxTile(zoom)

        return MTTileBounds(
            minX = (minX - buffer).coerceAtLeast(0),
            minY = (minY - buffer).coerceAtLeast(0),
            maxX = (maxX + buffer).coerceAtMost(maxIdx),
            maxY = (maxY + buffer).coerceAtMost(maxIdx),
        )
    }

    /**
     * Applies a buffer around a set of tiles.
     */
    fun applyBuffer(
        tiles: Set<MTTileIndex>,
        buffer: Int,
    ): Set<MTTileIndex> {
        if (buffer <= 0) return tiles
        val result = mutableSetOf<MTTileIndex>()
        for (tile in tiles) {
            val maxIdx = safeMaxTile(tile.z)
            for (dx in -buffer..buffer) {
                for (dy in -buffer..buffer) {
                    val nx = (tile.x + dx).coerceIn(0, maxIdx)
                    val ny = (tile.y + dy).coerceIn(0, maxIdx)
                    result.add(MTTileIndex(nx, ny, tile.z))
                }
            }
        }
        return result
    }

    /**
     * Finds tiles intersected by a line segment using Amanatides-Woo DDA algorithm.
     */
    fun tilesIntersectingSegment(
        p1X: Double,
        p1Y: Double,
        p2X: Double,
        p2Y: Double,
        zoom: Int,
    ): Set<MTTileIndex> {
        val result = mutableSetOf<MTTileIndex>()
        val maxIdx = safeMaxTile(zoom)

        var x = floor(p1X).toInt()
        var y = floor(p1Y).toInt()

        val endX = floor(p2X).toInt()
        val endY = floor(p2Y).toInt()

        result.add(MTTileIndex(x.coerceIn(0, maxIdx), y.coerceIn(0, maxIdx), zoom))

        val dx = p2X - p1X
        val dy = p2Y - p1Y

        val stepX =
            if (dx > 0) {
                1
            } else if (dx < 0) {
                -1
            } else {
                0
            }
        val stepY =
            if (dy > 0) {
                1
            } else if (dy < 0) {
                -1
            } else {
                0
            }

        val tDeltaX = if (stepX != 0) abs(1.0 / dx) else Double.MAX_VALUE
        val tDeltaY = if (stepY != 0) abs(1.0 / dy) else Double.MAX_VALUE

        var tMaxX = if (stepX > 0) (floor(p1X) + 1.0 - p1X) * tDeltaX else (p1X - floor(p1X)) * tDeltaX
        var tMaxY = if (stepY > 0) (floor(p1Y) + 1.0 - p1Y) * tDeltaY else (p1Y - floor(p1Y)) * tDeltaY

        if (tMaxX.isNaN() || tMaxX == Double.POSITIVE_INFINITY) tMaxX = Double.MAX_VALUE
        if (tMaxY.isNaN() || tMaxY == Double.POSITIVE_INFINITY) tMaxY = Double.MAX_VALUE

        if (tMaxX == 0.0) tMaxX += tDeltaX
        if (tMaxY == 0.0) tMaxY += tDeltaY

        while (x != endX || y != endY) {
            if (tMaxX < tMaxY) {
                tMaxX += tDeltaX
                x += stepX
            } else if (tMaxY < tMaxX) {
                tMaxY += tDeltaY
                y += stepY
            } else {
                x += stepX
                y += stepY
                tMaxX += tDeltaX
                tMaxY += tDeltaY
            }
            result.add(MTTileIndex(x.coerceIn(0, maxIdx), y.coerceIn(0, maxIdx), zoom))
        }
        return result
    }

    /**
     * Calculates exactly which tiles cover a given route.
     */
    fun tilesForRoute(
        route: List<LngLat>,
        zoom: Int,
        buffer: Int = 1,
    ): Set<MTTileIndex> {
        if (route.isEmpty()) return emptySet()

        if (route.size == 1) {
            val x = longitudeToTileX(route[0].lng, zoom)
            val y = latitudeToTileY(route[0].lat, zoom)
            return applyBuffer(setOf(MTTileIndex(x, y, zoom)), buffer)
        }

        val tiles = mutableSetOf<MTTileIndex>()
        val zoomDouble = zoom.coerceIn(0, 62).toDouble()
        for (i in 0 until route.size - 1) {
            val p1 = route[i]
            val p2 = route[i + 1]

            val x1 = MTMath.longitudeToTileX(p1.lng, zoomDouble, false)
            val y1 = MTMath.latitudeToTileY(p1.lat, zoomDouble, false)
            val x2 = MTMath.longitudeToTileX(p2.lng, zoomDouble, false)
            val y2 = MTMath.latitudeToTileY(p2.lat, zoomDouble, false)

            tiles.addAll(tilesIntersectingSegment(x1, y1, x2, y2, zoom))
        }
        return applyBuffer(tiles, buffer)
    }

    private fun isPointInPolygon(
        point: LngLat,
        polygon: List<LngLat>,
    ): Boolean {
        var isInside = false
        var j = polygon.size - 1
        for (i in polygon.indices) {
            val pi = polygon[i]
            val pj = polygon[j]
            if ((pi.lat > point.lat) != (pj.lat > point.lat) &&
                point.lng < (pj.lng - pi.lng) * (point.lat - pi.lat) / (pj.lat - pi.lat) + pi.lng
            ) {
                isInside = !isInside
            }
            j = i
        }
        return isInside
    }

    /**
     * Calculates exactly which tiles cover a given polygon.
     */
    fun tilesForPolygon(
        polygon: List<LngLat>,
        zoom: Int,
        buffer: Int = 1,
    ): Set<MTTileIndex> {
        if (polygon.size <= 2) {
            return tilesForRoute(polygon, zoom, buffer)
        }

        val bbox = MTBoundingBox.fromCoordinates(polygon)
        val bounds = tileBounds(bbox, zoom, 0)

        val closedPolygon = polygon.toMutableList()
        if (polygon.first() != polygon.last()) {
            closedPolygon.add(polygon.first())
        }

        val tilesSet = tilesForRoute(closedPolygon, zoom, 0).toMutableSet()

        val n = 2.0.pow(zoom.toDouble())
        for (y in bounds.minY..bounds.maxY) {
            for (x in bounds.minX..bounds.maxX) {
                val tile = MTTileIndex(x, y, zoom)
                if (!tilesSet.contains(tile)) {
                    val lon = (x.toDouble() + 0.5) / n * 360.0 - 180.0
                    val latRad = atan(sinh(PI * (1.0 - 2.0 * (y.toDouble() + 0.5) / n)))
                    val lat = latRad * 180.0 / PI

                    if (isPointInPolygon(LngLat(lon, lat), closedPolygon)) {
                        tilesSet.add(tile)
                    }
                }
            }
        }

        return applyBuffer(tilesSet, buffer)
    }

    /**
     * Resolves tiles for any geometry type.
     */
    fun tiles(
        geometry: MTOfflineRegionGeometry,
        zoom: Int,
        paddingMeters: Double?,
    ): Set<MTTileIndex> {
        val buffer = calculateTileBuffer(paddingMeters, geometry.bbox, zoom)
        return tiles(geometry, zoom, buffer)
    }

    /**
     * Resolves tiles for any geometry type.
     */
    fun tiles(
        geometry: MTOfflineRegionGeometry,
        zoom: Int,
        buffer: Int = 1,
    ): Set<MTTileIndex> =
        when (geometry) {
            is MTOfflineRegionGeometry.BoundingBox -> {
                val bounds = tileBounds(geometry.bbox, zoom, buffer)
                val result = mutableSetOf<MTTileIndex>()
                for (x in bounds.minX..bounds.maxX) {
                    for (y in bounds.minY..bounds.maxY) {
                        result.add(MTTileIndex(x, y, zoom))
                    }
                }
                result
            }
            is MTOfflineRegionGeometry.Route -> tilesForRoute(geometry.coordinates, zoom, buffer)
            is MTOfflineRegionGeometry.Polygon -> tilesForPolygon(geometry.coordinates, zoom, buffer)
        }

    /**
     * Computes the exact total number of tiles required to cover a geometry over a range of zooms.
     */
    fun estimateTileCount(
        geometry: MTOfflineRegionGeometry,
        zoomRange: MTOfflineZoomRange,
        paddingMeters: Double?,
    ): Int {
        return if (geometry is MTOfflineRegionGeometry.BoundingBox) {
            estimateTileCount(geometry.bbox, zoomRange, paddingMeters)
        } else {
            var totalTiles = 0
            for (zoom in zoomRange.minZoom..zoomRange.maxZoom) {
                val buffer = calculateTileBuffer(paddingMeters, geometry.bbox, zoom)
                totalTiles += tiles(geometry, zoom, buffer).size
            }
            totalTiles
        }
    }

    /**
     * Computes the exact total number of tiles required to cover a geometry over a range of zooms.
     */
    fun estimateTileCount(
        geometry: MTOfflineRegionGeometry,
        zoomRange: MTOfflineZoomRange,
        buffer: Int = 1,
    ): Int {
        return if (geometry is MTOfflineRegionGeometry.BoundingBox) {
            estimateTileCount(geometry.bbox, zoomRange, buffer)
        } else {
            var totalTiles = 0
            for (zoom in zoomRange.minZoom..zoomRange.maxZoom) {
                totalTiles += tiles(geometry, zoom, buffer).size
            }
            totalTiles
        }
    }

    /**
     * Computes the exact total number of tiles required to cover a bounding box over a range of zooms.
     */
    fun estimateTileCount(
        bbox: MTBoundingBox,
        zoomRange: MTOfflineZoomRange,
        paddingMeters: Double?,
    ): Int {
        val normalizedBoxes = bbox.normalizedAndSplit()
        var totalTiles = 0

        for (box in normalizedBoxes) {
            for (zoom in zoomRange.minZoom..zoomRange.maxZoom) {
                val buffer = calculateTileBuffer(paddingMeters, box, zoom)
                val bounds = tileBounds(box, zoom, buffer)
                val countX = bounds.maxX - bounds.minX + 1
                val countY = bounds.maxY - bounds.minY + 1
                totalTiles += countX * countY
            }
        }
        return totalTiles
    }

    /**
     * Computes the exact total number of tiles required to cover a bounding box over a range of zooms.
     */
    fun estimateTileCount(
        bbox: MTBoundingBox,
        zoomRange: MTOfflineZoomRange,
        buffer: Int = 1,
    ): Int {
        val normalizedBoxes = bbox.normalizedAndSplit()
        var totalTiles = 0

        for (box in normalizedBoxes) {
            for (zoom in zoomRange.minZoom..zoomRange.maxZoom) {
                val bounds = tileBounds(box, zoom, buffer)
                val countX = bounds.maxX - bounds.minX + 1
                val countY = bounds.maxY - bounds.minY + 1
                totalTiles += countX * countY
            }
        }
        return totalTiles
    }

    /**
     * Computes the exact number of tiles required per zoom level.
     */
    fun estimateTileCountPerZoom(
        bbox: MTBoundingBox,
        zoomRange: MTOfflineZoomRange,
        paddingMeters: Double?,
    ): Map<Int, Int> {
        val normalizedBoxes = bbox.normalizedAndSplit()
        val counts = mutableMapOf<Int, Int>()

        for (box in normalizedBoxes) {
            for (zoom in zoomRange.minZoom..zoomRange.maxZoom) {
                val buffer = calculateTileBuffer(paddingMeters, box, zoom)
                val bounds = tileBounds(box, zoom, buffer)
                val countX = bounds.maxX - bounds.minX + 1
                val countY = bounds.maxY - bounds.minY + 1
                val current = counts.getOrDefault(zoom, 0)
                counts[zoom] = current + (countX * countY)
            }
        }
        return counts
    }

    /**
     * Computes the exact number of tiles required per zoom level.
     */
    fun estimateTileCountPerZoom(
        bbox: MTBoundingBox,
        zoomRange: MTOfflineZoomRange,
        buffer: Int = 1,
    ): Map<Int, Int> {
        val normalizedBoxes = bbox.normalizedAndSplit()
        val counts = mutableMapOf<Int, Int>()

        for (box in normalizedBoxes) {
            for (zoom in zoomRange.minZoom..zoomRange.maxZoom) {
                val bounds = tileBounds(box, zoom, buffer)
                val countX = bounds.maxX - bounds.minX + 1
                val countY = bounds.maxY - bounds.minY + 1
                val current = counts.getOrDefault(zoom, 0)
                counts[zoom] = current + (countX * countY)
            }
        }
        return counts
    }
}
