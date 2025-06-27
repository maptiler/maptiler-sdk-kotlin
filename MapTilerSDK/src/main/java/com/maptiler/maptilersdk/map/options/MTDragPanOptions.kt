/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import kotlinx.serialization.Serializable

@Serializable
data class MTDragPanOptions(
    /**
     * Factor used to scale the drag velocity.
     *
     * Default: 0.
     */
    val linearity: Double? = null,
    /**
     * The maximum value of the drag velocity.
     *
     * Default: 1400
     */
    val maxSpeed: Double? = null,
    /**
     * The rate at which the speed reduces after the pan ends.
     *
     * Default: 2500
     */
    val deceleration: Double? = null,
)
