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
import com.maptiler.maptilersdk.map.types.MTPoint

internal data class PanBy(
    val offset: MTPoint,
) : MTCommand {
    override fun toJS(): String {
        val offsetString: JSString = JsonConfig.json.encodeToString(offset)

        return "${MTBridge.MAP_OBJECT}.panBy($offsetString);"
    }
}
