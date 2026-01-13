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
    val data: String,
    val layerId: String? = null,
    val sourceId: String? = null,
    val beforeId: String? = null,
    val minzoom: Double? = null,
    val maxzoom: Double? = null,
    val outline: Boolean? = null,
    val outlineColor: MTStringOrZoomStringValues? = null,
    val outlineWidth: MTNumberOrZoomNumberValues? = null,
    val outlineOpacity: MTNumberOrZoomNumberValues? = null,
    // PointLayerOptions
    val pointColor: MTColorValue? = null,
    val pointColorRamp: MTColorRamp? = null,
    val pointRadius: MTNumberOrZoomNumberValues? = null,
    val minPointRadius: Double? = null,
    val maxPointRadius: Double? = null,
    val property: String? = null,
    val pointOpacity: MTNumberOrZoomNumberValues? = null,
    val alignOnViewport: Boolean? = null,
    val cluster: Boolean? = null,
    val showLabel: Boolean? = null,
    val labelColor: MTColorValue? = null,
    val labelSize: Double? = null,
    val zoomCompensation: Boolean? = null,
) {
    // Backward-compat constructors for previous simple types
    @Suppress("LongParameterList")
    constructor(
        data: String,
        layerId: String? = null,
        sourceId: String? = null,
        beforeId: String? = null,
        minzoom: Double? = null,
        maxzoom: Double? = null,
        outline: Boolean? = null,
        outlineColor: String? = null,
        outlineWidth: Double? = null,
        outlineOpacity: Double? = null,
        pointColor: String? = null,
        pointColorRamp: MTColorRamp? = null,
        pointRadius: Double? = null,
        minPointRadius: Double? = null,
        maxPointRadius: Double? = null,
        property: String? = null,
        pointOpacity: Double? = null,
        alignOnViewport: Boolean? = null,
        cluster: Boolean? = null,
        showLabel: Boolean? = null,
        labelColor: String? = null,
        labelSize: Double? = null,
        zoomCompensation: Boolean? = null,
    ) : this(
        data = data,
        layerId = layerId,
        sourceId = sourceId,
        beforeId = beforeId,
        minzoom = minzoom,
        maxzoom = maxzoom,
        outline = outline,
        outlineColor = outlineColor?.let { MTStringOrZoomStringValues.StringValue(it) },
        outlineWidth = outlineWidth?.let { MTNumberOrZoomNumberValues.Number(it) },
        outlineOpacity = outlineOpacity?.let { MTNumberOrZoomNumberValues.Number(it) },
        pointColor = pointColor?.let { MTColorValue(it) },
        pointColorRamp = pointColorRamp,
        pointRadius = pointRadius?.let { MTNumberOrZoomNumberValues.Number(it) },
        minPointRadius = minPointRadius,
        maxPointRadius = maxPointRadius,
        property = property,
        pointOpacity = pointOpacity?.let { MTNumberOrZoomNumberValues.Number(it) },
        alignOnViewport = alignOnViewport,
        cluster = cluster,
        showLabel = showLabel,
        labelColor = labelColor?.let { MTColorValue(it) },
        labelSize = labelSize,
        zoomCompensation = zoomCompensation,
    )
}
