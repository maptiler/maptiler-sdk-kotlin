/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.LngLat

internal data class Project(
    val coordinate: LngLat,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String =
        // Return a plain object literal so WebView can JSON-serialize it.
        "(() => { const p = ${MTBridge.MAP_OBJECT}.project([${coordinate.lng}, ${coordinate.lat}]); return { x: p.x, y: p.y }; })();"
}
