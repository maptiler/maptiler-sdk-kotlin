/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

/**
 * Shared defaults and utilities for vector layer helpers.
 * Centralizes the Kotlin-side fallback values used by helper wrappers.
 */
internal interface MTVectorLayerHelper {
    // Default values for shared properties
    val defaultMinZoom: Double
        get() = 0.0

    val defaultMaxZoom: Double
        get() = 23.0

    val defaultOutline: Boolean
        get() = false

    val defaultOutlineColor: MTStringOrZoomStringValues
        get() = MTStringOrZoomStringValues.ColorHex(MTColorValue("#FFFFFF"))

    val defaultOutlineWidth: MTNumberOrZoomNumberValues
        get() = MTNumberOrZoomNumberValues.Number(1.0)

    val defaultOutlineOpacity: MTNumberOrZoomNumberValues
        get() = MTNumberOrZoomNumberValues.Number(1.0)

    /**
     * Apply common defaults to point helper options when not provided.
     */
    fun withCommonDefaults(options: MTPointLayerOptions): MTPointLayerOptions =
        options.copy(
            minzoom = options.minzoom ?: defaultMinZoom,
            maxzoom = options.maxzoom ?: defaultMaxZoom,
            outline = options.outline ?: defaultOutline,
            outlineColor = options.outlineColor ?: defaultOutlineColor,
            outlineWidth = options.outlineWidth ?: defaultOutlineWidth,
            outlineOpacity = options.outlineOpacity ?: defaultOutlineOpacity,
        )

    /**
     * Apply common defaults to heatmap helper options when not provided.
     */
    fun withCommonDefaults(options: MTHeatmapLayerOptions): MTHeatmapLayerOptions =
        options.copy(
            minzoom = options.minzoom ?: defaultMinZoom,
            maxzoom = options.maxzoom ?: defaultMaxZoom,
        )

    /**
     * Apply common defaults to polyline helper options when not provided.
     */
    fun withCommonDefaults(options: MTPolylineLayerOptions): MTPolylineLayerOptions =
        options.copy(
            minzoom = options.minzoom ?: defaultMinZoom,
            maxzoom = options.maxzoom ?: defaultMaxZoom,
            outline = options.outline ?: defaultOutline,
            outlineColor = options.outlineColor ?: defaultOutlineColor,
            outlineWidth = options.outlineWidth ?: defaultOutlineWidth,
            outlineOpacity = options.outlineOpacity ?: defaultOutlineOpacity,
        )

    /**
     * Apply common defaults to polygon helper options when not provided.
     */
    fun withCommonDefaults(options: MTPolygonLayerOptions): MTPolygonLayerOptions =
        options.copy(
            minzoom = options.minzoom ?: defaultMinZoom,
            maxzoom = options.maxzoom ?: defaultMaxZoom,
            outline = options.outline ?: defaultOutline,
            outlineColor = options.outlineColor ?: defaultOutlineColor,
            outlineWidth = options.outlineWidth ?: defaultOutlineWidth,
            outlineOpacity = options.outlineOpacity ?: defaultOutlineOpacity,
        )
}
