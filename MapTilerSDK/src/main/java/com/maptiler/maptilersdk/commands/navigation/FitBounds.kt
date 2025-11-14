/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTFitBoundsOptions
import com.maptiler.maptilersdk.map.types.MTBounds

internal data class FitBounds(
    val bounds: MTBounds,
    val options: MTFitBoundsOptions? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val boundsString = JsonConfig.json.encodeToString(bounds)
        val optionsString =
            options?.let {
                ",${JsonConfig.json.encodeToString(it)}"
            } ?: ""

        return "${MTBridge.MAP_OBJECT}.fitBounds($boundsString$optionsString);"
    }
}
