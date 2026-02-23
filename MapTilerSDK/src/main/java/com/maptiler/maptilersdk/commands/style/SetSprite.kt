/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class SetSprite(
    val spriteUrl: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val formattedUrl = spriteUrl.replace("'", "\\'")
        return "${MTBridge.MAP_OBJECT}.setSprite('$formattedUrl');"
    }
}
