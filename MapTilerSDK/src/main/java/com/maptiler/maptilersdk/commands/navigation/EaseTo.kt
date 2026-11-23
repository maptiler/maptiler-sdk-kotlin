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
import com.maptiler.maptilersdk.map.options.MTAnimationOptions
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

internal data class EaseTo(
    val cameraOptions: MTCameraOptions,
    val animationOptions: MTAnimationOptions? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val cameraOptionsString: JSString =
            if (animationOptions == null) {
                JsonConfig.json.encodeToString(cameraOptions)
            } else {
                val camera = JsonConfig.json.encodeToJsonElement(cameraOptions).jsonObject
                val animation = JsonConfig.json.encodeToJsonElement(animationOptions).jsonObject
                JsonConfig.json.encodeToString(JsonObject(camera + animation))
            }

        return "${MTBridge.MAP_OBJECT}.easeTo($cameraOptionsString);"
    }
}
