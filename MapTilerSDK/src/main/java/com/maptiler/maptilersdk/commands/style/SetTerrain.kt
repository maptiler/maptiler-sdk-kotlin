/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.style.MTTerrainSpecification
import kotlinx.serialization.encodeToString

internal data class SetTerrain(
    val terrain: MTTerrainSpecification?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        return if (terrain != null) {
            val terrainString = JsonConfig.json.encodeToString(terrain)
            "${MTBridge.MAP_OBJECT}.setTerrain($terrainString);"
        } else {
            "${MTBridge.MAP_OBJECT}.setTerrain(null);"
        }
    }
}
