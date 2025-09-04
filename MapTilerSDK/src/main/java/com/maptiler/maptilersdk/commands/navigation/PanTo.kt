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
import kotlinx.serialization.json.Json

internal data class PanTo(
    val coordinates: LngLat,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        // Pretty-printed JSON to match expected JS contract in tests
        val prettyJson =
            Json {
                prettyPrint = true
                encodeDefaults = JsonConfig.json.configuration.encodeDefaults
                explicitNulls = JsonConfig.json.configuration.explicitNulls
            }
        val coordinatesString: JSString = prettyJson.encodeToString(coordinates)

        return "${MTBridge.MAP_OBJECT}.panTo($coordinatesString);"
    }
}
