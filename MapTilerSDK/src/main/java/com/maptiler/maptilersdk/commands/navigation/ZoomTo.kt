/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTAnimationOptions
import kotlinx.serialization.encodeToString

internal data class ZoomTo(
    val zoom: Double,
    val options: MTAnimationOptions? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        return if (options != null) {
            val optionsString: JSString = JsonConfig.json.encodeToString(options)
            "${MTBridge.MAP_OBJECT}.zoomTo($zoom, $optionsString);"
        } else {
            "${MTBridge.MAP_OBJECT}.zoomTo($zoom);"
        }
    }
}
