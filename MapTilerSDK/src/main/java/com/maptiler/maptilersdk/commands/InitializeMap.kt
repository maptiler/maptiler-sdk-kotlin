/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import com.maptiler.maptilersdk.map.types.MTLanguage

internal data class InitializeMap(
    val apiKey: String,
    val options: MTMapOptions?,
    val referenceStyle: MTMapReferenceStyle,
    val styleVariant: MTMapStyleVariant?,
    val isSessionLogicEnabled: Any?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

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

        var optionsString: JSString = JsonConfig.json.encodeToString(options)

        if (options?.language is MTLanguage.Special) {
            optionsString =
                optionsString.trimIndent().replace(
                    Regex("""("language":\s*)"([^"]*)""""),
                    "$1$2",
                )
        }

        val eventLevel = options?.eventLevel?.toString() ?: "ESSENTIAL"
        val throttleMs = options?.highFrequencyEventThrottleMs ?: 0
        return "initializeMap('$apiKey', $styleString, $optionsString, $isSessionLogicEnabled, '$eventLevel', $throttleMs);"
    }
}
