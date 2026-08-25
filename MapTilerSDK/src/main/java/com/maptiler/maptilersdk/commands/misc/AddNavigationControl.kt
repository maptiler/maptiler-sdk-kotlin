/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.types.MTMapCorner
import com.maptiler.maptilersdk.utils.JsonConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

internal data class AddNavigationControl(
    val showCompass: Boolean = true,
    val showZoom: Boolean = true,
    val visualizePitch: Boolean = false,
    val position: MTMapCorner = MTMapCorner.TOP_RIGHT,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val options =
            JsonConfig.json.encodeToString(
                NavigationControlOptions.serializer(),
                NavigationControlOptions(showCompass, showZoom, visualizePitch),
            )
        val positionStr = JsonConfig.json.encodeToString(MTMapCorner.serializer(), position)
        return "${MTBridge.MAP_OBJECT}.addControl(new ${MTBridge.SDK_OBJECT}.MaptilerNavigationControl($options), $positionStr);"
    }
}

@Serializable
private data class NavigationControlOptions(
    val showCompass: Boolean,
    val showZoom: Boolean,
    val visualizePitch: Boolean,
)
