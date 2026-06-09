/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.helpers.MTMath
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.types.MTBounds
import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * A bounding box in WGS84 coordinates.
 */
@Serializable
data class MTBoundingBox(
    val minLon: Double,
    val minLat: Double,
    val maxLon: Double,
    val maxLat: Double,
) {
    /**
     * Returns `true` if the bounding box spans across the antimeridian (180th meridian).
     */
    val crossesAntimeridian: Boolean
        get() = minLon > maxLon

    /**
     * Checks if this bounding box intersects with another bounding box.
     *
     * @param other The other bounding box to check against.
     * @return `true` if the bounding boxes intersect, otherwise `false`.
     */
    fun intersects(other: MTBoundingBox): Boolean {
        val lonIntersects =
            if (this.crossesAntimeridian) {
                if (other.crossesAntimeridian) {
                    true
                } else {
                    other.minLon <= this.maxLon || other.maxLon >= this.minLon
                }
            } else if (other.crossesAntimeridian) {
                this.minLon <= other.maxLon || this.maxLon >= other.minLon
            } else {
                !(this.minLon > other.maxLon || this.maxLon < other.minLon)
            }
        val latIntersects = !(this.minLat > other.maxLat || this.maxLat < other.minLat)
        return lonIntersects && latIntersects
    }

    /**
     * Normalizes the bounding box coordinates and splits it into two if it crosses the antimeridian (Dateline).
     *
     * @return A list containing one normalized bounding box, or two if it crosses the antimeridian.
     */
    fun normalizedAndSplit(): List<MTBoundingBox> {
        val cMinLat = clampLatitude(minLat)
        val cMaxLat = clampLatitude(maxLat)

        // Handle bounding boxes covering or exceeding the entire globe's width
        if ((maxLon - minLon) >= 360.0) {
            return listOf(MTBoundingBox(-180.0, cMinLat, 180.0, cMaxLat))
        }

        val nMinLon = normalizeLongitude(minLon)
        val nMaxLon = normalizeLongitude(maxLon)
        val normalizedBox = MTBoundingBox(nMinLon, cMinLat, nMaxLon, cMaxLat)

        return if (normalizedBox.crossesAntimeridian) {
            listOf(
                MTBoundingBox(nMinLon, cMinLat, 180.0, cMaxLat),
                MTBoundingBox(-180.0, cMinLat, nMaxLon, cMaxLat),
            )
        } else {
            listOf(normalizedBox)
        }
    }

    /**
     * Returns an equivalent [MTBounds] instance.
     */
    fun toBounds(): MTBounds =
        MTBounds(
            southwest = LngLat(minLon, minLat),
            northeast = LngLat(maxLon, maxLat),
        )

    /**
     * Expands the bounding box outward by a given distance in meters.
     *
     * @param meters The distance in meters to expand.
     * @return A new expanded bounding box.
     */
    fun expanded(meters: Double): MTBoundingBox {
        if (meters <= 0) return this
        val latPadding = MTMath.toDegrees(meters / MTMath.EARTH_RADIUS)

        val midLat = (minLat + maxLat) / 2.0
        val cosMidLat = cos(MTMath.toRadians(midLat))

        val lonPadding =
            if (cosMidLat > 0.01) {
                MTMath.toDegrees(meters / MTMath.EARTH_RADIUS) / cosMidLat
            } else {
                180.0
            }

        val newMinLat = clampLatitude(minLat - latPadding)
        val newMaxLat = clampLatitude(maxLat + latPadding)

        val newMinLon: Double
        val newMaxLon: Double

        if (lonPadding >= 180.0) {
            newMinLon = -180.0
            newMaxLon = 180.0
        } else {
            newMinLon = normalizeLongitude(minLon - lonPadding)
            newMaxLon = normalizeLongitude(maxLon + lonPadding)
        }

        return MTBoundingBox(newMinLon, newMinLat, newMaxLon, newMaxLat)
    }

    /**
     * Expands the bounding box outward by a given percentage.
     *
     * @param percentage The percentage to expand (e.g., 0.1 for a 10% buffer).
     * @return A new expanded bounding box.
     */
    fun expandedByPercentage(percentage: Double): MTBoundingBox {
        val latSpan = maxLat - minLat
        val lonSpan =
            if (crossesAntimeridian) {
                (180.0 - minLon) + (maxLon - (-180.0))
            } else {
                maxLon - minLon
            }

        val latPad = latSpan * percentage
        val lonPad = lonSpan * percentage

        val newMinLat = clampLatitude(minLat - latPad)
        val newMaxLat = clampLatitude(maxLat + latPad)
        val newMinLon = normalizeLongitude(minLon - lonPad)
        val newMaxLon = normalizeLongitude(maxLon + lonPad)

        return MTBoundingBox(newMinLon, newMinLat, newMaxLon, newMaxLat)
    }

    /**
     * Calculates the approximate surface area in square kilometers.
     */
    fun areaInSquareKilometers(): Double {
        val earthRadiusKm = 6371.0
        val lat1 = minLat * PI / 180.0
        val lat2 = maxLat * PI / 180.0

        val lonSpan =
            if (crossesAntimeridian) {
                (180.0 - minLon) + (maxLon - (-180.0))
            } else {
                maxLon - minLon
            }

        val lonSpanRad = lonSpan * PI / 180.0
        return earthRadiusKm.pow(2) * lonSpanRad * abs(sin(lat2) - sin(lat1))
    }

    /**
     * Estimates the exact number of tiles required for this bounding box within the specified zoom range.
     *
     * @param zoomRange The min and max zoom levels.
     * @return The total tile count.
     */
    fun estimatedTileCount(zoomRange: MTOfflineZoomRange): Int = MTTileMath.estimateTileCount(this, zoomRange)

    /**
     * Estimates the number of tiles required per zoom level for this bounding box.
     *
     * @param zoomRange The min and max zoom levels.
     * @return A map each zoom level to its tile count.
     */
    fun estimatedTileCountPerZoom(zoomRange: MTOfflineZoomRange): Map<Int, Int> = MTTileMath.estimateTileCountPerZoom(this, zoomRange)

    companion object {
        /**
         * The maximum latitude limit for Web Mercator projection.
         */
        const val MAX_WEB_MERCATOR_LAT = MTMath.MAX_SAFE_LATITUDE

        /**
         * Normalizes a longitude to the standard range [-180, 180].
         */
        fun normalizeLongitude(longitude: Double): Double = MTMath.wrapLongitude(longitude)

        /**
         * Clamps a latitude to the Web Mercator valid range.
         */
        fun clampLatitude(latitude: Double): Double = latitude.coerceIn(-MAX_WEB_MERCATOR_LAT, MAX_WEB_MERCATOR_LAT)

        /**
         * Creates a bounding box that contains all the given coordinates.
         */
        fun fromCoordinates(coordinates: List<LngLat>): MTBoundingBox {
            if (coordinates.isEmpty()) {
                return MTBoundingBox(0.0, 0.0, 0.0, 0.0)
            }

            var minLon = Double.MAX_VALUE
            var minLat = Double.MAX_VALUE
            var maxLon = -Double.MAX_VALUE
            var maxLat = -Double.MAX_VALUE

            for (coord in coordinates) {
                minLon = minOf(minLon, coord.lng)
                minLat = minOf(minLat, coord.lat)
                maxLon = maxOf(maxLon, coord.lng)
                maxLat = maxOf(maxLat, coord.lat)
            }

            return MTBoundingBox(minLon, minLat, maxLon, maxLat)
        }

        /**
         * Initializes a bounding box from an [MTBounds] instance.
         */
        fun fromBounds(bounds: MTBounds): MTBoundingBox =
            MTBoundingBox(
                minLon = bounds.southwest.lng,
                minLat = bounds.southwest.lat,
                maxLon = bounds.northeast.lng,
                maxLat = bounds.northeast.lat,
            )
    }
}
