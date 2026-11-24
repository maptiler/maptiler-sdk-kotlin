/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

/**
 * Command to query source features.
 */
internal data class QuerySourceFeatures(
    val sourceId: String,
    val sourceLayer: String? = null,
    val filter: String? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val options = QueryOptions(sourceLayer, filter)
        val optionsJson = JsonConfig.json.encodeToString(options)

        return "JSON.stringify(${MTBridge.MAP_OBJECT}.querySourceFeatures(\"$sourceId\", $optionsJson));"
    }

    @Serializable
    private data class QueryOptions(
        val sourceLayer: String?,
        val filter: String?,
    )
}
