/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.LngLat
import java.util.Locale

/**
 * Command to query terrain elevation at a specific coordinate.
 */
internal data class QueryTerrainElevation(
    val coordinates: LngLat,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val lngStr = "%.6f".format(Locale.US, coordinates.lng)
        val latStr = "%.6f".format(Locale.US, coordinates.lat)
        return "${MTBridge.MAP_OBJECT}.queryTerrainElevation([$lngStr, $latStr]);"
    }
}
