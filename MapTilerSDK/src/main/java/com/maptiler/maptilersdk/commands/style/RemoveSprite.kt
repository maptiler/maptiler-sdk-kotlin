/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class RemoveSprite(
    val identifier: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val sanitizedIdentifier = identifier.replace("\\", "\\\\").replace("'", "\\'")

        return "${MTBridge.MAP_OBJECT}.removeSprite('$sanitizedIdentifier');"
    }
}
