/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import com.maptiler.maptilersdk.colorramp.MTColorRamp
import com.maptiler.maptilersdk.map.style.MTStyle

/**
 * Helper for creating a heatmap visualization layer from data and styling options.
 */
class MTHeatmapLayerHelper(
    private val style: MTStyle,
) {
    /**
     * Adds a heatmap layer based on the provided options.
     */
    fun addHeatmap(options: MTHeatmapLayerOptions) = style.addHeatmapLayer(options)

    /**
     * Adds a heatmap layer using a color ramp.
     */
    fun addHeatmap(
        options: MTHeatmapLayerOptions,
        colorRamp: MTColorRamp?,
    ) = style.addHeatmapLayer(options.copy(colorRamp = colorRamp))
}
