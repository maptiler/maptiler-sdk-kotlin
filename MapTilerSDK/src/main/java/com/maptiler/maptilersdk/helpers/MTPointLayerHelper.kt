/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import com.maptiler.maptilersdk.colorramp.MTColorRamp
import com.maptiler.maptilersdk.map.style.MTStyle

/**
 * Helper for creating a point visualization layer from data and styling options.
 *
 * This uses the current style to create the underlying source and layers.
 */
class MTPointLayerHelper(
    private val style: MTStyle,
) : MTVectorLayerHelper {
    /**
     * Adds a point layer based on the provided options.
     */
    fun addPoint(options: MTPointLayerOptions) = style.addPointLayer(withCommonDefaults(options))

    /**
     * Adds a point layer using a color ramp for pointColor.
     */
    fun addPoint(
        options: MTPointLayerOptions,
        colorRamp: MTColorRamp,
    ) = style.addPointLayer(withCommonDefaults(options.copy(pointColorRamp = colorRamp)))
}
