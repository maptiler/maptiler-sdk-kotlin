/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.navigable

import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import com.maptiler.maptilersdk.map.options.MTFitBoundsOptions
import com.maptiler.maptilersdk.map.options.MTFlyToOptions
import com.maptiler.maptilersdk.map.options.MTPaddingOptions
import com.maptiler.maptilersdk.map.types.MTBounds
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
     * Adjusts the camera to ensure the entire bounding box is visible.
     *
     * @param bounds Geographical area that must be visible once the transition completes.
     * @param options Optional animation and padding configuration applied while fitting the bounds.
     */
    fun fitBounds(
        bounds: MTBounds,
        options: MTFitBoundsOptions? = null,
    )

    /**
     * Returns the geographical bounds currently visible in the viewport.
     */
    suspend fun getBounds(): MTBounds

    /**
     * Fits the camera to the coarse bounds inferred from the user's public IP address.
     */
    fun fitToIpBounds()

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
     * Returns the map's maximum pitch.
     */
    suspend fun getMaxPitch(): Double

    /**
     * Returns the map's minimum pitch.
     */
    suspend fun getMinPitch(): Double

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
     * Returns whether the map is set to render multiple world copies.
     */
    suspend fun getRenderWorldCopies(): Boolean

    /**
     * Returns whether all currently requested tiles are loaded.
     */
    suspend fun areTilesLoaded(): Boolean

    /**
     * Returns the pixel ratio currently used to render the map.
     */
    suspend fun getPixelRatio(): Double

    /**
     * Returns the elevation of the map's center point in meters above sea level.
     */
    suspend fun getCenterElevation(): Double

    /**
     * Returns the geographical constraints currently applied to the map, if any.
     */
    suspend fun getMaxBounds(): MTBounds?

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
     * Overrides the pixel ratio used to render the map.
     *
     * @param pixelRatio Desired pixel ratio. Values above `1.0` improve sharpness, values below trade detail for performance.
     */
    fun setPixelRatio(pixelRatio: Double)

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

    /**
     * Applies or clears the geographical constraints that clamp user interaction.
     *
     * Passing `null` removes the bounds restriction.
     *
     * @param bounds Geographical limits for map interactions, or null to remove restrictions.
     */
    fun setMaxBounds(bounds: MTBounds?)
}
