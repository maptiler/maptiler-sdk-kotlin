/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class EnableTerrain(
    val exaggerationFactor: Double? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String =
        if (exaggerationFactor == null) {
            "${MTBridge.MAP_OBJECT}.enableTerrain();"
        } else {
            "${MTBridge.MAP_OBJECT}.enableTerrain($exaggerationFactor);"
        }
}
