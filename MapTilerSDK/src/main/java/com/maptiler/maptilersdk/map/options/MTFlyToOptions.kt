/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

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

    constructor(curve: Double?, minZoom: Double?, speed: Double?, screenSpeed: Double?, maxDuration: Double?) {
        this.curve = curve
        this.minZoom = minZoom
        this.speed = speed
        this.screenSpeed = screenSpeed
        this.maxDuration = maxDuration
    }
}
