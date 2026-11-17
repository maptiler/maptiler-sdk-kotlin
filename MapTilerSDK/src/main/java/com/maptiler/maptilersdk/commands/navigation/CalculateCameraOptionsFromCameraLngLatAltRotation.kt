package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.LngLat
import kotlinx.serialization.encodeToString

internal data class CalculateCameraOptionsFromCameraLngLatAltRotation(
    val lngLat: LngLat,
    val altitude: Double,
    val bearing: Double,
    val pitch: Double,
    val roll: Double,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val lngLatString = JsonConfig.json.encodeToString(lngLat)

        return "JSON.stringify(${MTBridge.MAP_OBJECT}.calculateCameraOptionsFromCameraLngLatAltRotation(" +
            "$lngLatString, $altitude, $bearing, $pitch, $roll));"
    }
}
