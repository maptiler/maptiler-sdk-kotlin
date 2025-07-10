/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.navigable

import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import com.maptiler.maptilersdk.map.options.MTFlyToOptions
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
     *@param cameraOptions Options for controlling the desired location, zoom, bearing, and pitch of the camera.
     */
    fun easeTo(cameraOptions: MTCameraOptions)
}
