/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTFitBoundsOptions
import com.maptiler.maptilersdk.map.types.MTPoint
import kotlinx.serialization.encodeToString

internal data class FitScreenCoordinates(
    val p0: MTPoint,
    val p1: MTPoint,
    val bearing: Double,
    val options: MTFitBoundsOptions? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val p0String = JsonConfig.json.encodeToString(p0)
        val p1String = JsonConfig.json.encodeToString(p1)
        val optionsString =
            options?.let {
                ",${JsonConfig.json.encodeToString(it)}"
            } ?: ""

        return "${MTBridge.MAP_OBJECT}.fitScreenCoordinates($p0String,$p1String,$bearing$optionsString);"
    }
}
