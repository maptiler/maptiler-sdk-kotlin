package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTFitBoundsOptions
import com.maptiler.maptilersdk.map.types.MTBounds
import kotlinx.serialization.encodeToString

internal data class CameraForBounds(
    val bounds: MTBounds,
    val options: MTFitBoundsOptions? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val boundsString = JsonConfig.json.encodeToString(bounds)
        val optionsString = options?.let { JsonConfig.json.encodeToString(it) } ?: "null"

        return "JSON.stringify(${MTBridge.MAP_OBJECT}.cameraForBounds(" +
            "$boundsString, $optionsString));"
    }
}
