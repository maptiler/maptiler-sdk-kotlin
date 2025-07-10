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
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import com.maptiler.maptilersdk.map.options.MTFlyToOptions
import kotlinx.serialization.Serializable

internal data class FlyTo(
    val cameraOptions: MTCameraOptions,
    val flyToOptions: MTFlyToOptions? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val surrogate = getSurrogate(cameraOptions, flyToOptions)
        val flyToString: JSString = JsonConfig.json.encodeToString(surrogate)

        return "${MTBridge.MAP_OBJECT}.flyTo($flyToString);"
    }

    private fun getSurrogate(
        cameraOptions: MTCameraOptions,
        flyToOptions: MTFlyToOptions?,
    ): FlyToSurrogate =
        FlyToSurrogate(
            cameraOptions.center,
            cameraOptions.zoom,
            cameraOptions.bearing,
            cameraOptions.pitch,
            flyToOptions?.curve,
            flyToOptions?.minZoom,
            flyToOptions?.speed,
            flyToOptions?.screenSpeed,
            flyToOptions?.maxDuration,
        )
}

@Serializable
private data class FlyToSurrogate(
    val center: LngLat,
    val zoom: Double?,
    val bearing: Double?,
    val pitch: Double?,
    val curve: Double?,
    val minZoom: Double?,
    val speed: Double?,
    val screenSpeed: Double?,
    val maxDuration: Double?,
)
