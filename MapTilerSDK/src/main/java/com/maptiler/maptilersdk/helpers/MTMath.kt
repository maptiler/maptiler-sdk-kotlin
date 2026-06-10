/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import com.maptiler.maptilersdk.map.LngLat
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Pure math helpers for geographic calculations, projections, and tile indices.
 */
object MTMath {
    /**
     * Radius of the Earth in meters.
     */
    const val EARTH_RADIUS: Double = 6378137.0

    /**
     * Circumference of the Earth in meters.
     */
    const val EARTH_CIRCUMFERENCE: Double = 2.0 * PI * EARTH_RADIUS

    /**
     * Maximum latitude used for Web Mercator calculations to prevent infinity.
     */
    const val MAX_SAFE_LATITUDE: Double = 85.05112877980659

    /**
     * Converts degrees to radians.
     */
    fun toRadians(degrees: Double): Double = (degrees * PI) / 180.0

    /**
     * Converts radians to degrees.
     */
    fun toDegrees(radians: Double): Double = (radians * 180.0) / PI

    /**
     * Converts a longitude to Web Mercator X coordinate in meters.
     */
    fun longitudeToMercatorX(longitude: Double): Double = (longitude * EARTH_CIRCUMFERENCE) / 360.0

    /**
     * Converts a latitude to Web Mercator Y coordinate in meters.
     */
    fun latitudeToMercatorY(latitude: Double): Double {
        val latRad = toRadians(latitude)
        val y = ln(tan((PI / 4.0) + (latRad / 2.0)))
        return (y * EARTH_CIRCUMFERENCE) / (2.0 * PI)
    }

    /**
     * Converts a WGS84 coordinate to Web Mercator (X, Y) in meters.
     */
    fun wgs84ToMercator(coordinate: LngLat): Pair<Double, Double> =
        Pair(
            longitudeToMercatorX(coordinate.lng),
            latitudeToMercatorY(coordinate.lat),
        )

    /**
     * Converts a Web Mercator X coordinate in meters to longitude.
     */
    fun mercatorXToLongitude(x: Double): Double = (x * 360.0) / EARTH_CIRCUMFERENCE

    /**
     * Converts a Web Mercator Y coordinate in meters to latitude.
     */
    fun mercatorYToLatitude(y: Double): Double {
        val latRad = atan(exp((y * 2.0 * PI) / EARTH_CIRCUMFERENCE)) * 2.0 - (PI / 2.0)
        return toDegrees(latRad)
    }

    /**
     * Converts a Web Mercator (X, Y) in meters to WGS84 coordinate.
     */
    fun mercatorToWgs84(
        x: Double,
        y: Double,
    ): LngLat =
        LngLat(
            lng = mercatorXToLongitude(x),
            lat = mercatorYToLatitude(y),
        )

    /**
     * Calculates the Web Mercator tile X coordinate for a given longitude and zoom level.
     */
    fun longitudeToTileX(
        longitude: Double,
        zoom: Double,
        round: Boolean = true,
    ): Double {
        val n = 2.0.pow(zoom)
        val x = ((longitude + 180.0) / 360.0) * n
        return if (round) floor(x) else x
    }

    /**
     * Calculates the Web Mercator tile Y coordinate (XYZ scheme) for a given latitude and zoom level.
     */
    fun latitudeToTileY(
        latitude: Double,
        zoom: Double,
        round: Boolean = true,
    ): Double {
        val n = 2.0.pow(zoom)
        val latRad = toRadians(latitude)
        val secLat = 1.0 / cos(latRad)
        val y = ((1.0 - ln(tan(latRad) + secLat) / PI) / 2.0) * n
        return if (round) floor(y) else y
    }

    /**
     * Converts a WGS84 coordinate to Web Mercator tile indices.
     */
    fun wgs84ToTileIndex(
        coordinate: LngLat,
        zoom: Double,
        round: Boolean = true,
    ): Pair<Double, Double> =
        Pair(
            longitudeToTileX(coordinate.lng, zoom, round),
            latitudeToTileY(coordinate.lat, zoom, round),
        )

    /**
     * Calculates the great-circle distance between two WGS84 coordinates using the Haversine formula.
     *
     * @return The distance in meters.
     */
    fun haversineDistanceWgs84(
        from: LngLat,
        to: LngLat,
    ): Double {
        val lat1 = toRadians(from.lat)
        val lat2 = toRadians(to.lat)
        val deltaLat = toRadians(to.lat - from.lat)
        val deltaLng = toRadians(to.lng - from.lng)

        val a =
            sin(deltaLat / 2.0) * sin(deltaLat / 2.0) +
                cos(lat1) * cos(lat2) * sin(deltaLng / 2.0) * sin(deltaLng / 2.0)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))

        return EARTH_RADIUS * c
    }

    /**
     * Calculates the total cumulated distance of a route made of multiple WGS84 coordinates.
     *
     * @return The total distance in meters.
     */
    fun haversineCumulatedDistanceWgs84(route: List<LngLat>): Double {
        if (route.size <= 1) return 0.0
        var distance = 0.0
        for (i in 0 until route.size - 1) {
            distance += haversineDistanceWgs84(route[i], route[i + 1])
        }
        return distance
    }

    /**
     * Computes an intermediate point between two WGS84 coordinates using the Haversine formula.
     *
     * @param ratio A value between 0.0 and 1.0.
     * @return The intermediate coordinate.
     */
    fun haversineIntermediateWgs84(
        from: LngLat,
        to: LngLat,
        ratio: Double,
    ): LngLat {
        val lon1 = toRadians(from.lng)
        val lat1 = toRadians(from.lat)
        val lon2 = toRadians(to.lng)
        val lat2 = toRadians(to.lat)

        val aValue =
            sin((lat1 - lat2) / 2.0) * sin((lat1 - lat2) / 2.0) +
                cos(lat1) * cos(lat2) * sin((lon1 - lon2) / 2.0) * sin((lon1 - lon2) / 2.0)
        val d = 2.0 * asin(sqrt(aValue))

        if (d == 0.0) return from

        val a = sin((1.0 - ratio) * d) / sin(d)
        val b = sin(ratio * d) / sin(d)

        val x = a * cos(lat1) * cos(lon1) + b * cos(lat2) * cos(lon2)
        val y = a * cos(lat1) * sin(lon1) + b * cos(lat2) * sin(lon2)
        val z = a * sin(lat1) + b * sin(lat2)

        val lat3 = atan2(z, sqrt(x.pow(2) + y.pow(2)))
        val lon3 = atan2(y, x)

        return LngLat(lng = toDegrees(lon3), lat = toDegrees(lat3))
    }

    /**
     * Calculates the Earth's circumference at a given latitude in meters.
     */
    fun circumferenceAtLatitude(latitude: Double): Double = EARTH_CIRCUMFERENCE * cos(toRadians(latitude))

    /**
     * Wraps a longitude value to be within the `[-180, 180]` range.
     */
    fun wrapLongitude(longitude: Double): Double {
        var lng = longitude % 360.0
        if (lng > 180.0) {
            lng -= 360.0
        } else if (lng < -180.0) {
            lng += 360.0
        }
        return lng
    }

    private val E_DOUBLE = exp(1.0)
}
