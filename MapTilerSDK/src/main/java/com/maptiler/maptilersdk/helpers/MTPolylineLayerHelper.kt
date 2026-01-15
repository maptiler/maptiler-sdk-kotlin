/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import com.maptiler.maptilersdk.map.style.MTStyle

/**
 * Helper for creating a polyline visualization layer from data and styling options.
 *
 * This uses the current style to create the underlying source and layers
 */
class MTPolylineLayerHelper(
    private val style: MTStyle,
) : MTVectorLayerHelper {
    /**
     * Adds a polyline layer based on the provided options.
     */
    fun addPolyline(options: MTPolylineLayerOptions) = style.addPolylineLayer(withCommonDefaults(options))
}
