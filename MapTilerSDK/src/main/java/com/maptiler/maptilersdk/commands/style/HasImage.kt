/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class HasImage(
    val identifier: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): JSString {
        val sanitizedIdentifier = identifier.replace("\\", "\\\\").replace("'", "\\'")
        return "${MTBridge.MAP_OBJECT}.hasImage('$sanitizedIdentifier');"
    }
}
