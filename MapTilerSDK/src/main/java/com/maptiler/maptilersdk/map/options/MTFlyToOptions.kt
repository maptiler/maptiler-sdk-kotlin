/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Options describing the destination and animation of the transition.
 */
@Serializable
class MTFlyToOptions {
    var curve: Double? = null
    var minZoom: Double? = null
    var speed: Double? = null
    var screenSpeed: Double? = null
    var maxDuration: Double? = null

    /**
     * Experimental: preload the tiles visible at the destination before the animation starts.
     *
     * Requires [com.maptiler.maptilersdk.map.MTMapOptions.useExperimentalTilePreloading]; ignored otherwise.
     * Every preloaded tile counts against your MapTiler Cloud API key quota.
     */
    @SerialName("experimental_preload")
    var experimentalPreload: MTTilePreloadOptions? = null

    constructor(curve: Double?, minZoom: Double?, speed: Double?, screenSpeed: Double?, maxDuration: Double?) {
        this.curve = curve
        this.minZoom = minZoom
        this.speed = speed
        this.screenSpeed = screenSpeed
        this.maxDuration = maxDuration
    }

    constructor(
        curve: Double? = null,
        minZoom: Double? = null,
        speed: Double? = null,
        screenSpeed: Double? = null,
        maxDuration: Double? = null,
        experimentalPreload: MTTilePreloadOptions?,
    ) : this(curve, minZoom, speed, screenSpeed, maxDuration) {
        this.experimentalPreload = experimentalPreload
    }
}
