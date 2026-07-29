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

internal data class PointRotateAround(
    val point: MTPoint,
    val angle: Double,
    val pivot: MTPoint,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): JSString =
        "(() => { " +
            "const p = new ${MTBridge.SDK_OBJECT}.Point(${point.x}, ${point.y})" +
            ".rotateAround($angle, new ${MTBridge.SDK_OBJECT}.Point(${pivot.x}, ${pivot.y})); " +
            "return { x: p.x, y: p.y }; " +
            "})();"
}
