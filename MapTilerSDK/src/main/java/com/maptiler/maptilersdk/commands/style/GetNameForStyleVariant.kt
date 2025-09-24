/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant

internal data class GetNameForStyleVariant(
    val styleVariant: MTMapStyleVariant,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val variantName = styleVariant.name
        return "${MTBridge.SDK_OBJECT}.${MTBridge.STYLE_OBJECT}.getNameForStyleVariant('$variantName');"
    }
}
