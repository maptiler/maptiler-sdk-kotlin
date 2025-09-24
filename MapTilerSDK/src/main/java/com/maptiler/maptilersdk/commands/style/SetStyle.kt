/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant

internal data class SetStyle(
    val referenceStyle: MTMapReferenceStyle,
    val styleVariant: MTMapStyleVariant?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val referenceStyleName = referenceStyle.getName()
        val styleVariantName = styleVariant?.value?.uppercase()

        val styleString =
            if (referenceStyle.isCustom()) {
                "'$referenceStyleName'"
            } else {
                val style =
                    if (styleVariant != null) {
                        "$referenceStyleName.$styleVariantName"
                    } else {
                        referenceStyleName
                    }
                "${MTBridge.SDK_OBJECT}.${MTBridge.STYLE_OBJECT}.$style"
            }

        return "${MTBridge.MAP_OBJECT}.setStyle($styleString);"
    }
}
