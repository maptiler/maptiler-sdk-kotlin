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

internal data class PointMatMult(
    val point: MTPoint,
    val matrix: List<Double>,
) : MTCommand {
    init {
        require(matrix.size == 4) { "Matrix must contain exactly 4 elements." }
    }

    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): JSString {
        val m0 = matrix[0]
        val m1 = matrix[1]
        val m2 = matrix[2]
        val m3 = matrix[3]
        return "(() => { " +
            "const p = new ${MTBridge.SDK_OBJECT}.Point(${point.x}, ${point.y})" +
            ".matMult([$m0, $m1, $m2, $m3]); " +
            "return { x: p.x, y: p.y }; " +
            "})();"
    }
}
