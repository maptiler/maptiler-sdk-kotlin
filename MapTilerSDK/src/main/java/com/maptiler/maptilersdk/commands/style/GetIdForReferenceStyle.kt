/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle

internal data class GetIdForReferenceStyle(
    val referenceStyle: MTMapReferenceStyle,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val refName = referenceStyle.getName()
        return "${MTBridge.SDK_OBJECT}.${MTBridge.STYLE_OBJECT}.getIdForReferenceStyle('$refName');"
    }
}
