/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.types.MTPoint

internal data class Unproject(
    val point: MTPoint,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String =
        // Return a plain object literal so WebView can JSON-serialize it.
        "(() => { const p = ${MTBridge.MAP_OBJECT}.unproject([${point.x}, ${point.y}]); return { lng: p.lng, lat: p.lat }; })();"
}
