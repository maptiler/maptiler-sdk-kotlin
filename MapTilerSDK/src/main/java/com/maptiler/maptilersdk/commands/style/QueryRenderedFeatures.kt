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
import com.maptiler.maptilersdk.offline.MTBoundingBox
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.util.Locale

/**
 * Command to query rendered features at a specific point or bounding box.
 */
internal class QueryRenderedFeatures private constructor(
    val point: MTPoint?,
    val bbox: MTBoundingBox?,
    val layers: List<String>? = null,
    val filter: String? = null,
) : MTCommand {
    constructor(point: MTPoint, layers: List<String>? = null, filter: String? = null) : this(point, null, layers, filter)
    constructor(bbox: MTBoundingBox, layers: List<String>? = null, filter: String? = null) : this(null, bbox, layers, filter)

    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val options = QueryOptions(layers, filter)
        val optionsJson = JsonConfig.json.encodeToString(options)

        val geometryJS =
            if (point != null) {
                val xStr = "%.6f".format(Locale.US, point.x)
                val yStr = "%.6f".format(Locale.US, point.y)
                "[$xStr, $yStr]"
            } else if (bbox != null) {
                val minLon = "%.6f".format(Locale.US, bbox.minLon)
                val minLat = "%.6f".format(Locale.US, bbox.minLat)
                val maxLon = "%.6f".format(Locale.US, bbox.maxLon)
                val maxLat = "%.6f".format(Locale.US, bbox.maxLat)
                "[${MTBridge.MAP_OBJECT}.project([$minLon, $minLat]), ${MTBridge.MAP_OBJECT}.project([$maxLon, $maxLat])]"
            } else {
                "[]"
            }

        return "JSON.stringify(${MTBridge.MAP_OBJECT}.queryRenderedFeatures($geometryJS, $optionsJson));"
    }

    @Serializable
    private data class QueryOptions(
        val layers: List<String>?,
        val filter: String?,
    )
}
