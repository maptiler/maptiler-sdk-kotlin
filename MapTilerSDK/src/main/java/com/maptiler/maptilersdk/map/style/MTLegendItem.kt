/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style

import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * A single legend entry describing one [MTLayerCategory] in the current style.
 *
 * @property category The category this entry represents.
 * @property name Display name of the entry (defaults to [MTLayerCategory.defaultName]).
 * @property layerIds Style layers belonging to the category, in rendering order.
 * @property isVisible `true` if at least one of the layers is visible.
 * @property color Representative CSS color (e.g. `#aad3df`, `hsl(205, 56%, 73%)`), or `null` if none could be resolved.
 */
data class MTLegendItem(
    val category: MTLayerCategory,
    val name: String,
    val layerIds: List<String>,
    val isVisible: Boolean,
    val color: String?,
)

internal object MTLegendColorResolver {
    private val colorPrefixes = listOf("#", "rgb(", "rgba(", "hsl(", "hsla(")

    /**
     * Layer types in order of preference for picking a legend swatch, with the paint property holding their color.
     */
    val swatchPaintProperties: List<Pair<MTLayerType, String>> =
        listOf(
            MTLayerType.FILL to "fill-color",
            MTLayerType.LINE to "line-color",
            MTLayerType.FILL_EXTRUSION to "fill-extrusion-color",
            MTLayerType.CIRCLE to "circle-color",
            MTLayerType.HILLSHADE to "hillshade-shadow-color",
            MTLayerType.SYMBOL to "text-color",
        )

    /**
     * Source layers that best represent a category, most representative first (e.g. wood rather than ice for nature).
     */
    private val preferredSourceLayers: Map<MTLayerCategory, List<String>> =
        mapOf(
            MTLayerCategory.WATER to listOf("water"),
            MTLayerCategory.NATURE to listOf("wood", "forest", "park", "grass", "landcover", "globallandcover"),
            MTLayerCategory.LANDUSE to listOf("residential", "landuse"),
            MTLayerCategory.ROADS to listOf("road", "transportation"),
            MTLayerCategory.TRANSIT to listOf("railway"),
            MTLayerCategory.BOUNDARIES to listOf("country_border", "boundary"),
        )

    /**
     * Picks the layer and paint property that best represent [category] visually: the most preferred layer type
     * first, then the most representative source layer, then rendering order.
     */
    fun swatchSource(
        category: MTLayerCategory,
        layers: List<MTStyleLayerInfo>,
    ): Pair<MTStyleLayerInfo, String>? {
        val preferred = preferredSourceLayers[category].orEmpty()
        val rank = { layer: MTStyleLayerInfo ->
            preferred.indexOf(layer.sourceLayer).let { if (it < 0) preferred.size else it }
        }

        for ((type, property) in swatchPaintProperties) {
            val layer = layers.filter { it.type == type }.minByOrNull(rank) ?: continue
            return layer to property
        }
        return null
    }

    /**
     * Returns the first literal color found in a property value. Handles plain colors as well as zoom
     * expressions (`interpolate`, `step`, `match`, legacy `stops`).
     */
    fun firstColor(value: JsonElement?): String? =
        when (value) {
            is JsonPrimitive ->
                value.content.takeIf { value.isString && isColor(it) }
            is JsonArray -> value.firstNotNullOfOrNull { firstColor(it) }
            is JsonObject -> firstColor(value["stops"]) ?: value.values.firstNotNullOfOrNull { firstColor(it) }
            else -> null
        }

    private fun isColor(value: String): Boolean {
        val lower = value.trim().lowercase()
        return colorPrefixes.any { lower.startsWith(it) }
    }
}
