/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style

import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import kotlinx.serialization.Serializable

/**
 * Read-only snapshot of a layer in the current map style.
 *
 * Covers every layer of the style (including the built-in MapTiler layers), not only layers added through the SDK.
 *
 * @property id Unique layer identifier.
 * @property type Layer type, or `null` if the type is not known to the SDK (see [rawType]).
 * @property rawType Layer type as reported by the style (e.g. `fill`, `line`).
 * @property source Identifier of the source the layer reads from, if any.
 * @property sourceLayer Vector tile layer name (e.g. `water`, `landcover`), if any.
 * @property isVisible Whether the layer's `visibility` layout property is `visible`.
 * @property minZoom Minimum zoom level at which the layer is rendered, if set.
 * @property maxZoom Maximum zoom level at which the layer is rendered, if set.
 */
data class MTStyleLayerInfo(
    val id: String,
    val type: MTLayerType?,
    val rawType: String,
    val source: String?,
    val sourceLayer: String?,
    val isVisible: Boolean,
    val minZoom: Double?,
    val maxZoom: Double?,
)

@Serializable
internal data class MTStyleLayerInfoSurrogate(
    val id: String,
    val type: String,
    val source: String? = null,
    val sourceLayer: String? = null,
    val visibility: String = "visible",
    val minzoom: Double? = null,
    val maxzoom: Double? = null,
) {
    fun toInfo(): MTStyleLayerInfo =
        MTStyleLayerInfo(
            id = id,
            type = runCatching { JsonConfig.json.decodeFromString<MTLayerType>("\"$type\"") }.getOrNull(),
            rawType = type,
            source = source,
            sourceLayer = sourceLayer,
            isVisible = visibility != "none",
            minZoom = minzoom,
            maxZoom = maxzoom,
        )
}
