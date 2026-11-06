/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style

import com.maptiler.maptilersdk.map.style.layer.MTLayerType

/**
 * Describes which style layers to select.
 *
 * Every non-null criterion must match (AND). Within a single criterion any value may match (OR).
 * An empty query (all criteria `null`) matches every layer.
 *
 * Example: all line layers drawing water
 * ```
 * MTLayerQuery(categories = setOf(MTLayerCategory.WATER), types = setOf(MTLayerType.LINE))
 * ```
 *
 * @property ids Exact layer identifiers.
 * @property idContains Case-insensitive substring of the layer identifier.
 * @property types Layer types.
 * @property sources Source identifiers.
 * @property sourceLayers Vector tile source layer names (e.g. `water`, `transportation`).
 * @property categories Semantic layer categories, see [MTLayerCategory].
 */
data class MTLayerQuery(
    val ids: Set<String>? = null,
    val idContains: String? = null,
    val types: Set<MTLayerType>? = null,
    val sources: Set<String>? = null,
    val sourceLayers: Set<String>? = null,
    val categories: Set<MTLayerCategory>? = null,
) {
    /**
     * Returns whether [layer] satisfies this query.
     */
    fun matches(layer: MTStyleLayerInfo): Boolean =
        (ids == null || layer.id in ids) &&
            (idContains == null || layer.id.contains(idContains, ignoreCase = true)) &&
            (types == null || (layer.type != null && layer.type in types)) &&
            (sources == null || (layer.source != null && layer.source in sources)) &&
            (sourceLayers == null || (layer.sourceLayer != null && layer.sourceLayer in sourceLayers)) &&
            (categories == null || categories.any { it.matches(layer) })

    companion object {
        /**
         * Query matching every layer of the given [categories].
         */
        fun of(vararg categories: MTLayerCategory): MTLayerQuery = MTLayerQuery(categories = categories.toSet())
    }
}
