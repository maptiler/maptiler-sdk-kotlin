/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTCameraOptions

internal data class JumpTo(
    val cameraOptions: MTCameraOptions,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val cameraOptionsString: JSString = JsonConfig.json.encodeToString(cameraOptions)

        return "${MTBridge.MAP_OBJECT}.jumpTo($cameraOptionsString);"
    }
}
