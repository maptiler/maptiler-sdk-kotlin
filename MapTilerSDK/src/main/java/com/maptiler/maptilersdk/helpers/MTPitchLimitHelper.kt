/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import com.maptiler.maptilersdk.map.MTMapViewController

/**
 * Helper object to configure pitch limits on the map.
 */
object MTPitchLimitHelper {
    /**
     * Easily limits the pitch of the map.
     *
     * @param controller The map view controller to apply the limits to.
     * @param minPitch The minimum pitch limit (0 to 85 degrees). Defaults to 0.0.
     * @param maxPitch The maximum pitch limit (0 to 180 degrees). Defaults to 85.0.
     */
    fun limitPitch(
        controller: MTMapViewController,
        minPitch: Double = 0.0,
        maxPitch: Double = 85.0,
    ) {
        require(minPitch >= 0.0) { "minPitch must be >= 0.0" }
        require(maxPitch <= 180.0) { "maxPitch must be <= 180.0" }
        require(minPitch <= maxPitch) { "minPitch cannot be greater than maxPitch" }

        // Set the pitch limits
        controller.setMinPitch(minPitch)
        controller.setMaxPitch(maxPitch)
    }
}
