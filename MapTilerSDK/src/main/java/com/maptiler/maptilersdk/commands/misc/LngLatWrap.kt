/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.LngLat

internal data class LngLatWrap(
    val lngLat: LngLat,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): JSString = "new ${MTBridge.SDK_OBJECT}.LngLat(${lngLat.lng}, ${lngLat.lat}).wrap();"
}
