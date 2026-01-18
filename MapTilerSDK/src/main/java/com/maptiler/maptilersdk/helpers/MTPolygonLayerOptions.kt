/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import com.maptiler.maptilersdk.map.style.layer.line.MTLineCap
import com.maptiler.maptilersdk.map.style.layer.line.MTLineJoin
import kotlinx.serialization.Serializable

/**
 * Options for building a polygon visualization layer through the helper.
 */
@Serializable
@Suppress("LongParameterList")
data class MTPolygonLayerOptions(
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
    // Polygon specific
    val fillColor: MTStringOrZoomStringValues? = null,
    val fillOpacity: MTNumberOrZoomNumberValues? = null,
    val outlinePosition: MTOutlinePosition? = null,
    val outlineDashArray: MTDashArrayOption? = null,
    val outlineCap: MTLineCap? = null,
    val outlineJoin: MTLineJoin? = null,
    val pattern: String? = null,
    val outlineBlur: MTNumberOrZoomNumberValues? = null,
) {
    /**
     * Convenience constructor for simpler types (String colors and Double numerics, dash/cap/join/pattern as strings).
     */
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
        fillColor: String? = null,
        fillOpacity: Double? = null,
        outlinePosition: String? = null,
        outlineDashPattern: String? = null,
        outlineCap: String? = null,
        outlineJoin: String? = null,
        pattern: String? = null,
        outlineBlur: Double? = null,
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
        fillColor = fillColor?.let { MTStringOrZoomStringValues.StringValue(it) },
        fillOpacity = fillOpacity?.let { MTNumberOrZoomNumberValues.Number(it) },
        outlinePosition = outlinePosition?.toMTOutlinePositionOrNull(),
        outlineDashArray = outlineDashPattern?.let { MTDashArrayOption.StringValue(it) },
        outlineCap = outlineCap?.toMTLineCapOrNull(),
        outlineJoin = outlineJoin?.toMTLineJoinOrNull(),
        pattern = pattern,
        outlineBlur = outlineBlur?.let { MTNumberOrZoomNumberValues.Number(it) },
    )
}

/**
 * Outline position relative to polygon edge.
 */
@Serializable
enum class MTOutlinePosition(val jsName: String) {
    CENTER("center"),
    INSIDE("inside"),
    OUTSIDE("outside"),
}

private fun String.toMTOutlinePositionOrNull(): MTOutlinePosition? =
    when (lowercase()) {
        "center" -> MTOutlinePosition.CENTER
        "inside" -> MTOutlinePosition.INSIDE
        "outside" -> MTOutlinePosition.OUTSIDE
        else -> null
    }

private fun String.toMTLineCapOrNull(): MTLineCap? =
    when (lowercase()) {
        "butt" -> MTLineCap.BUTT
        "round" -> MTLineCap.ROUND
        "square" -> MTLineCap.SQUARE
        else -> null
    }

private fun String.toMTLineJoinOrNull(): MTLineJoin? =
    when (lowercase()) {
        "bevel" -> MTLineJoin.BEVEL
        "round" -> MTLineJoin.ROUND
        "miter" -> MTLineJoin.MITER
        else -> null
    }
