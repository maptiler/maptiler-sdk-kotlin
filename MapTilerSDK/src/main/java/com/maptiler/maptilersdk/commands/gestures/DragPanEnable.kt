/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.gestures

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTDragPanOptions

internal data class DragPanEnable(
    val options: MTDragPanOptions?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val optionsString: JSString = JsonConfig.json.encodeToString(options)

        return "${MTBridge.MAP_OBJECT}.dragPan.enable($optionsString);"
    }
}
