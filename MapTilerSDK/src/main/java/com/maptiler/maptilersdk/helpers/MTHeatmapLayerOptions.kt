/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import com.maptiler.maptilersdk.colorramp.MTColorRamp
import kotlinx.serialization.Serializable

/**
 * A zoom/value pair used for ramping numeric properties.
 */
@Serializable
data class MTZoomNumberValue(
    val zoom: Double,
    val value: Double,
)

/**
 * A property/value pair used for property driven styling.
 */
@Serializable
data class MTPropertyValueStop(
    val propertyValue: Double,
    val value: Double,
)

/**
 * Numeric values that can be expressed as a constant, zoom-stops or property-stops.
 */
sealed class MTHeatmapValue {
    data class Constant(val value: Double) : MTHeatmapValue()

    @Serializable
    data class ZoomValues(val values: List<MTZoomNumberValue>) : MTHeatmapValue() {
        init {
            require(values.isNotEmpty()) { "Zoom values cannot be empty." }
        }
    }

    @Serializable
    data class PropertyValues(val values: List<MTPropertyValueStop>) : MTHeatmapValue() {
        init {
            require(values.isNotEmpty()) { "Property values cannot be empty." }
        }
    }
}

/**
 * Options for building a heatmap visualization layer through the helper.
 */
data class MTHeatmapLayerOptions(
    /**
     * A geojson feature collection string or URL/UUID to be resolved.
     */
    val data: String,
    val layerId: String? = null,
    val sourceId: String? = null,
    val beforeId: String? = null,
    val minzoom: Double? = null,
    val maxzoom: Double? = null,
    val colorRamp: MTColorRamp? = null,
    val property: String? = null,
    val weight: MTHeatmapValue? = null,
    val radius: MTHeatmapValue? = null,
    val opacity: MTHeatmapValue? = null,
    val intensity: MTHeatmapValue? = null,
    val zoomCompensation: Boolean? = null,
)
