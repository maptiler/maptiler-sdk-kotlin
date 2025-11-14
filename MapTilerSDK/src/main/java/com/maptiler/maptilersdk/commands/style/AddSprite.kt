/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.formatUrlForJs
import java.net.URL

internal data class AddSprite(
    val identifier: String,
    val spriteUrl: URL,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val sanitizedIdentifier = identifier.replace("\\", "\\\\").replace("'", "\\'")
        val formattedUrl = formatUrlForJs(spriteUrl)

        return "${MTBridge.MAP_OBJECT}.addSprite('$sanitizedIdentifier', '$formattedUrl');"
    }
}
