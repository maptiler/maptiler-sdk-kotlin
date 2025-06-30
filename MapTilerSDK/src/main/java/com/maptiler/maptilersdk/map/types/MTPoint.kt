/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.types

import kotlinx.serialization.Serializable

/**
 * Two numbers representing x and y screen coordinates in pixels.
 */
@Serializable
data class MTPoint(
    val x: Double,
    val y: Double,
)
