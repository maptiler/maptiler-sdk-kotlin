/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.types.MTMapCorner
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

internal data class AddAttributionControl(
    val compact: Boolean? = null,
    val customAttribution: String? = null,
    val position: MTMapCorner = MTMapCorner.BOTTOM_RIGHT,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val options =
            JsonConfig.json.encodeToString(
                AttributionControlOptions.serializer(),
                AttributionControlOptions(compact, customAttribution),
            )
        val positionStr = JsonConfig.json.encodeToString(MTMapCorner.serializer(), position)
        return "${MTBridge.MAP_OBJECT}.addControl(new ${MTBridge.SDK_OBJECT}.AttributionControl($options), $positionStr);"
    }
}

@Serializable
private data class AttributionControlOptions(
    val compact: Boolean? = null,
    val customAttribution: String? = null,
)
