/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class SetTerrainExaggeration(
    val exaggeration: Double,
    val animate: Boolean? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String =
        if (animate == null) {
            "${MTBridge.MAP_OBJECT}.setTerrainExaggeration($exaggeration);"
        } else {
            "${MTBridge.MAP_OBJECT}.setTerrainExaggeration($exaggeration, $animate);"
        }
}
