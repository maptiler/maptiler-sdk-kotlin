/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.style.source.MTImageSource

internal data class SetCoordinatesToImageSource(
    val coordinates: List<LngLat>,
    val source: MTImageSource,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val coords: List<List<Double>> = coordinates.map { listOf(it.lng, it.lat) }
        val coordsString: JSString = JsonConfig.json.encodeToString(coords)
        return "${MTBridge.MAP_OBJECT}.getSource('${source.identifier}').setCoordinates($coordsString);"
    }
}
