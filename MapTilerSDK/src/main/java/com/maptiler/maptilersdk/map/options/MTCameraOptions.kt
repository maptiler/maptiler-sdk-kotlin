/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import com.maptiler.maptilersdk.map.LngLat
import kotlinx.serialization.Serializable

/**
 * Options for controlling the desired location, zoom, bearing, and pitch of the camera.
 */
@Serializable
class MTCameraOptions {
    /**
     * Geographical center of the map.
     */
    var center: LngLat
        private set

    /**
     * Zoom level of the map.
     */
    var zoom: Double? = null
        private set

    /**
     * The bearing of the map, measured in degrees counter-clockwise from north.
     */
    var bearing: Double? = null
        private set

    /**
     * The pitch (tilt) of the map, measured in degrees away from the plane of the screen (0-85).
     */
    var pitch: Double? = null
        private set

    /** Initializes camera options with center, zoom, bearing and pitch. */
    constructor(center: LngLat, zoom: Double? = null, bearing: Double? = null, pitch: Double? = null) {
        this.center = center
        this.zoom = zoom
        this.bearing = bearing
        this.pitch = pitch
    }
}
