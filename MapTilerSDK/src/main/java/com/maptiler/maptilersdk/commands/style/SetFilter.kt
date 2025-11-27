/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.style.dsl.PropertyValue

internal data class SetFilter(
    val layerId: String,
    val filter: PropertyValue,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${MTBridge.MAP_OBJECT}.setFilter(\"$layerId\", ${filter.asCode()});"
}
