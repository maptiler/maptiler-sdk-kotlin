/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.types.MTPoint

internal data class PointAngleTo(
    val point1: MTPoint,
    val point2: MTPoint,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): JSString =
        "new ${MTBridge.SDK_OBJECT}.Point(${point1.x}, ${point1.y})" +
            ".angleTo(new ${MTBridge.SDK_OBJECT}.Point(${point2.x}, ${point2.y}));"
}
