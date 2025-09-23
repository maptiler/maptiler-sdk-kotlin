/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.types.MTLanguage

internal data class SetLanguage(
    val language: MTLanguage,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        var languageString: JSString = JsonConfig.json.encodeToString(language)

        if (language is MTLanguage.Special) {
            languageString =
                languageString.trimIndent().replace(
                    Regex("""^\"(.*)\"$"""),
                    "$1",
                )
        }

        return "${MTBridge.MAP_OBJECT}.setLanguage($languageString);"
    }
}
