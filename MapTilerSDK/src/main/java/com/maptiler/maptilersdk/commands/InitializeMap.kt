/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import kotlinx.serialization.json.Json

data class InitializeMap(
    val apiKey: String,
    val options: MTMapOptions?,
    val referenceStyle: MTMapReferenceStyle,
    val styleVariant: MTMapStyleVariant?,
    val isSessionLogicEnabled: Any?,
) : MTCommand {
    override fun toJS(): JSString {
        val referenceStyleName = referenceStyle.getName()
        val styleVariantName = styleVariant?.value?.uppercase()

        var styleString = ""

        if (referenceStyle.isCustom()) {
            styleString = "'$referenceStyleName'"
        } else {
            var style = ""
            style =
                if (styleVariant != null) {
                    "$referenceStyleName.$styleVariantName"
                } else {
                    referenceStyleName
                }

            styleString = "${MTBridge.SDK_OBJECT}.${MTBridge.STYLE_OBJECT}.$style"
        }

        val optionsString: JSString = Json.encodeToString(options)

        return "initializeMap('$apiKey', $styleString, $optionsString, $isSessionLogicEnabled);"
    }
}
