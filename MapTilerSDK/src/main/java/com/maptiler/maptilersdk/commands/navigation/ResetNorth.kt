/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTAnimationOptions
import kotlinx.serialization.encodeToString

internal data class ResetNorth(
    val options: MTAnimationOptions? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        return if (options != null) {
            val optionsStr = JsonConfig.json.encodeToString(options)
            "${MTBridge.MAP_OBJECT}.resetNorth($optionsStr);"
        } else {
            "${MTBridge.MAP_OBJECT}.resetNorth();"
        }
    }
}
