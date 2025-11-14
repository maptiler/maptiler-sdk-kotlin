/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import com.maptiler.maptilersdk.map.types.MTPoint
import kotlinx.serialization.Serializable

/**
 * Options that control how the map camera fits a set of bounds.
 */
@Serializable
data class MTFitBoundsOptions(
    val padding: MTPaddingOptions? = null,
    val maxZoom: Double? = null,
    val linear: Boolean? = null,
    val offset: MTPoint? = null,
    val duration: Double? = null,
    val bearing: Double? = null,
    val pitch: Double? = null,
    val zoom: Double? = null,
)
