/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

/**
 * Sets the global light properties for the style.
 *
 * This command expects a raw JSON object string representing light options
 * compatible with MapLibre GL's Light specification.
 */
internal data class SetLight(
    val lightOptionsJson: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${MTBridge.MAP_OBJECT}.setLight($lightOptionsJson);"
}
