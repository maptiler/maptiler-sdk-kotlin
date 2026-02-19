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
data class MTAnimationOptions(
    /**
     * The animation's duration, measured in milliseconds.
     */
    val duration: Double? = null,
    /**
     * If false, no animation will occur.
     */
    val animate: Boolean? = null,
    /**
     * If true, then the animation is considered essential and will not be affected by prefers-reduced-motion.
     */
    val essential: Boolean? = null,
    /**
     * The center of the given bounds relative to the map's center, measured in pixels.
     */
    val offset: MTPoint? = null,
)
