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
import com.maptiler.maptilersdk.map.LngLat

internal data class PanTo(
    val coordinates: LngLat,
) : MTCommand {
    override fun toJS(): String {
        val coordinatesString: JSString = JsonConfig.json.encodeToString(coordinates)

        return "${MTBridge.MAP_OBJECT}.panTo($coordinatesString);"
    }
}
