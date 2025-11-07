/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.navigable

import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import com.maptiler.maptilersdk.map.options.MTFlyToOptions
import com.maptiler.maptilersdk.map.options.MTPaddingOptions
import com.maptiler.maptilersdk.map.types.MTPoint

/**
 * Defines methods for navigating the map.
 */
interface MTNavigable {
    /**
     * Pans the map by the specified offset.
     *
     * @param offset Offset to pan by.
     */
    fun panBy(offset: MTPoint)

    /**
     * Pans the map to the specified location with an animated transition.
     *
     * @param coordinates Coordinates to pan to.
     */
    fun panTo(coordinates: LngLat)

    /**
     * Changes any combination of center, zoom, bearing, and pitch, animating the transition along a curve that evokes flight.
     *
     * @param cameraOptions Options for controlling the desired location, zoom, bearing, and pitch of the camera.
     * @param flyToOptions Options describing the destination and animation of the transition.
     */
    fun flyTo(
        cameraOptions: MTCameraOptions,
        flyToOptions: MTFlyToOptions?,
    )

    /**
     * Changes any combination of center, zoom, bearing, and pitch, without an animated transition
     *
     * @param cameraOptions Options for controlling the desired location, zoom, bearing, and pitch of the camera.
     */
    fun jumpTo(cameraOptions: MTCameraOptions)

    /**
     * Changes any combination of center, zoom, bearing and pitch with an animated transition between old and new values.
     *
     * @param cameraOptions Options for controlling the desired location, zoom, bearing, and pitch of the camera.
     */
    fun easeTo(cameraOptions: MTCameraOptions)

    /**
     * Returns the map's current bearing.
     */
    suspend fun getBearing(): Double

    /**
     * Sets bearing of the map.
     *
     * @param bearing The bearing of the map, measured in degrees counter-clockwise from north.
     */
    fun setBearing(bearing: Double)

    /**
     * Returns the map's current roll.
     */
    suspend fun getRoll(): Double

    /**
     * Sets the map's roll angle.
     *
     * @param roll Desired roll.
     */
    fun setRoll(roll: Double)

    /**
     * Returns the map's current pitch.
     */
    suspend fun getPitch(): Double

    /**
     * Sets the map's pitch.
     *
     * @param pitch Desired pitch in degrees (0-85 default constraints).
     */
    fun setPitch(pitch: Double)

    /**
     * Returns the map's current center.
     */
    suspend fun getCenter(): LngLat

    /**
     * Returns whether the map's center is clamped to the ground.
     */
    suspend fun getCenterClampedToGround(): Boolean

    /**
     * Returns the elevation of the map's center point in meters above sea level.
     */
    suspend fun getCenterElevation(): Double

    /**
     * Projects geographical coordinates to a point on the container.
     *
     * @param coordinates The geographical coordinate to project.
     * @return Screen-space point in pixels.
     */
    suspend fun project(coordinates: LngLat): MTPoint

    /**
     * Sets the geographical center of the map.
     *
     * @param center Geographical center of the map.
     */
    fun setCenter(center: LngLat)

    /**
     * Sets the center clamped to the ground.
     *
     * If true, the elevation of the center point will automatically be set to the
     * terrain elevation (or zero if terrain is not enabled). If false, the elevation
     * of the center point will default to sea level and will not automatically update.
     *
     * @param isCenterClampedToGround Boolean indicating whether center is clamped to the ground.
     */
    fun setIsCenterClampedToGround(isCenterClampedToGround: Boolean)

    /**
     * Sets the elevation of the map's center point, in meters above sea level.
     *
     * @param elevation Desired elevation.
     */
    fun setCenterElevation(elevation: Double)

    /**
     * Sets the padding in pixels around the viewport.
     *
     * @param padding Custom options to use.
     */
    fun setPadding(padding: MTPaddingOptions)
}
