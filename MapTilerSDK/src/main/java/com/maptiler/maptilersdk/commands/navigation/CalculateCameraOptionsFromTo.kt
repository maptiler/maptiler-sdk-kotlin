package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.LngLat
import kotlinx.serialization.encodeToString

internal data class CalculateCameraOptionsFromTo(
    val from: LngLat,
    val altitudeFrom: Double,
    val to: LngLat,
    val altitudeTo: Double,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val fromString = JsonConfig.json.encodeToString(from)
        val toString = JsonConfig.json.encodeToString(to)

        return "JSON.stringify(${MTBridge.MAP_OBJECT}.calculateCameraOptionsFromTo(" +
            "$fromString, $altitudeFrom, $toString, $altitudeTo));"
    }
}
