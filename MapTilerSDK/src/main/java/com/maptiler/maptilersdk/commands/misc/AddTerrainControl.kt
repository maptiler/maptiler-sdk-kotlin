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

internal data class AddTerrainControl(
    val position: MTMapCorner = MTMapCorner.TOP_RIGHT,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val positionStr = JsonConfig.json.encodeToString(MTMapCorner.serializer(), position)
        return "${MTBridge.MAP_OBJECT}.addControl(new ${MTBridge.SDK_OBJECT}.MaptilerTerrainControl(), $positionStr);"
    }
}
