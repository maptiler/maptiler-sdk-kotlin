/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import com.maptiler.maptilersdk.map.types.MTPoint
import kotlinx.serialization.Serializable

/**
 * Options describing the animation of a transition.
 *
 * @property duration The animation's duration, measured in milliseconds.
 * @property animate If false, no animation will occur.
 * @property essential If true, then the animation is considered essential and will not be affected by prefers-reduced-motion.
 * @property offset The center of the given bounds relative to the map's center, measured in pixels.
 */
@Serializable
data class MTAnimationOptions(
    val duration: Long? = null,
    val animate: Boolean? = null,
    val essential: Boolean? = null,
    val offset: MTPoint? = null,
)
