/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import com.maptiler.maptilersdk.map.types.MTPoint
import kotlinx.serialization.Serializable

/**
 * Options describing the animation of the transition.
 */
@Serializable
class MTAnimationOptions {
    var offset: MTPoint? = null
    var animate: Boolean? = null
    var duration: Double? = null
    var essential: Boolean? = null

    constructor(
        offset: MTPoint? = null,
        animate: Boolean? = null,
        duration: Double? = null,
        essential: Boolean? = null,
    ) {
        this.offset = offset
        this.animate = animate
        this.duration = duration
        this.essential = essential
    }
}
