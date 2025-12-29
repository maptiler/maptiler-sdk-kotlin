/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import com.maptiler.maptilersdk.colorramp.MTColorRamp
import kotlinx.serialization.Serializable

/**
 * Options for building a point visualization layer through the helper.
 *
 * This mirrors the available configuration including common shape options.
 */
@Serializable
data class MTPointLayerOptions(
    // CommonShapeLayerOptions
    val data: String,
    val layerId: String? = null,
    val sourceId: String? = null,
    val beforeId: String? = null,
    val minzoom: Double? = null,
    val maxzoom: Double? = null,
    val outline: Boolean? = null,
    val outlineColor: String? = null,
    val outlineWidth: Double? = null,
    val outlineOpacity: Double? = null,
    // PointLayerOptions
    val pointColor: String? = null,
    val pointColorRamp: MTColorRamp? = null,
    val pointRadius: Double? = null,
    val minPointRadius: Double? = null,
    val maxPointRadius: Double? = null,
    val property: String? = null,
    val pointOpacity: Double? = null,
    val alignOnViewport: Boolean? = null,
    val cluster: Boolean? = null,
    val showLabel: Boolean? = null,
    val labelColor: String? = null,
    val labelSize: Double? = null,
    val zoomCompensation: Boolean? = null,
)
