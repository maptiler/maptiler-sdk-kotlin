/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class SourceExists(
    val sourceId: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String = "!!${MTBridge.MAP_OBJECT}.getSource('$sourceId');"
}
