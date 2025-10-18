/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTSpace

/**
 * Sets the globe space background (deep space / skybox configuration).
 * Unspecified fields keep their previous values.
 */
internal data class SetSpace(
    val space: MTSpace,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val json = JsonConfig.json.encodeToString(space)
        return "${MTBridge.MAP_OBJECT}.setSpace($json);"
    }
}
