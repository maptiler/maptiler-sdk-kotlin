/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import com.maptiler.maptilersdk.colorramp.MTColorRamp

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
    val weight: MTNumberOrPropertyValues? = null,
    val radius: MTRadiusOption? = null,
    val opacity: MTNumberOrZoomNumberValues? = null,
    val intensity: MTNumberOrZoomNumberValues? = null,
    val zoomCompensation: Boolean? = null,
) {
    // Backward-compat constructor with simple numeric fields
    @Suppress("LongParameterList")
    constructor(
        data: String,
        layerId: String? = null,
        sourceId: String? = null,
        beforeId: String? = null,
        minzoom: Double? = null,
        maxzoom: Double? = null,
        colorRamp: MTColorRamp? = null,
        property: String? = null,
        weight: Double? = null,
        radius: Double? = null,
        opacity: Double? = null,
        intensity: Double? = null,
        zoomCompensation: Boolean? = null,
    ) : this(
        data = data,
        layerId = layerId,
        sourceId = sourceId,
        beforeId = beforeId,
        minzoom = minzoom,
        maxzoom = maxzoom,
        colorRamp = colorRamp,
        property = property,
        weight = weight?.let { MTNumberOrPropertyValues.Number(it) },
        radius = radius?.let { MTRadiusOption.Number(it) },
        opacity = opacity?.let { MTNumberOrZoomNumberValues.Number(it) },
        intensity = intensity?.let { MTNumberOrZoomNumberValues.Number(it) },
        zoomCompensation = zoomCompensation,
    )
}
