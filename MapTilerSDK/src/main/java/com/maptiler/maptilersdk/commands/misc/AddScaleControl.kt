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
import com.maptiler.maptilersdk.map.types.MTScaleUnit
import kotlinx.serialization.Serializable

internal data class AddScaleControl(
    val maxWidth: Int? = null,
    val unit: MTScaleUnit? = null,
    val position: MTMapCorner = MTMapCorner.BOTTOM_LEFT,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val options =
            JsonConfig.json.encodeToString(
                ScaleControlOptions.serializer(),
                ScaleControlOptions(maxWidth, unit),
            )
        val positionStr = JsonConfig.json.encodeToString(MTMapCorner.serializer(), position)
        return "${MTBridge.MAP_OBJECT}.addControl(new ${MTBridge.SDK_OBJECT}.ScaleControl($options), $positionStr);"
    }
}

@Serializable
private data class ScaleControlOptions(
    val maxWidth: Int? = null,
    val unit: MTScaleUnit? = null,
)
