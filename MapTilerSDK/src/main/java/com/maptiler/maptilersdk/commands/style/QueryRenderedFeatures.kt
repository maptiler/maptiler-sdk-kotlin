/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.types.MTPoint
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

/**
 * Command to query rendered features at a specific point.
 */
internal data class QueryRenderedFeatures(
    val point: MTPoint,
    val layers: List<String>? = null,
    val filter: String? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val options = QueryOptions(layers, filter)
        val optionsJson = JsonConfig.json.encodeToString(options)

        // Ensure locale-invariant decimal points
        val xStr = "%.6f".format(java.util.Locale.US, point.x)
        val yStr = "%.6f".format(java.util.Locale.US, point.y)

        return "JSON.stringify(${MTBridge.MAP_OBJECT}.queryRenderedFeatures([$xStr, $yStr], $optionsJson));"
    }

    @Serializable
    private data class QueryOptions(
        val layers: List<String>?,
        val filter: String?,
    )
}
