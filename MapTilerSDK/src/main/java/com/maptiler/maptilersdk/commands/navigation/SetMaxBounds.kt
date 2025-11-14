/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.types.MTBounds

internal data class SetMaxBounds(
    val bounds: MTBounds?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val boundsString =
            bounds?.let {
                JsonConfig.json.encodeToString(it)
            } ?: "null"

        return "${MTBridge.MAP_OBJECT}.setMaxBounds($boundsString);"
    }
}
