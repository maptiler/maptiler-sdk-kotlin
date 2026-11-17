/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTRequestTransformRule

internal data class SetTransformRequest(
    val rules: List<MTRequestTransformRule>?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        return if (rules.isNullOrEmpty()) {
            "window.transformRequestRules = []; " +
                "${MTBridge.MAP_OBJECT}.setTransformRequest(undefined);"
        } else {
            val rulesJson = JsonConfig.json.encodeToString(rules)
            "window.transformRequestRules = $rulesJson; " +
                "${MTBridge.MAP_OBJECT}.setTransformRequest(window.createTransformRequestFn());"
        }
    }
}
