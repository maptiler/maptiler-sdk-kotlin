/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.helpers

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerOptions
import com.maptiler.maptilersdk.helpers.MTNumberOrPropertyValuesSerializer
import com.maptiler.maptilersdk.helpers.MTNumberOrZoomNumberValuesSerializer
import com.maptiler.maptilersdk.helpers.MTRadiusOptionSerializer
import kotlinx.serialization.builtins.serializer

internal data class AddHeatmapLayer(
    val options: MTHeatmapLayerOptions,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val optionsString: JSString = options.toJsObject()
        return "${MTBridge.SDK_OBJECT}.helpers.addHeatmap(${MTBridge.MAP_OBJECT}, $optionsString);"
    }
}

private fun MTHeatmapLayerOptions.toJsObject(): JSString {
    val json = JsonConfig.json
    val properties = mutableListOf<String>()

    properties.add("data: ${json.encodeToString(String.serializer(), data)}")
    layerId?.let { properties.add("layerId: ${json.encodeToString(String.serializer(), it)}") }
    sourceId?.let { properties.add("sourceId: ${json.encodeToString(String.serializer(), it)}") }
    beforeId?.let { properties.add("beforeId: ${json.encodeToString(String.serializer(), it)}") }
    minzoom?.let { properties.add("minzoom: ${json.encodeToString(Double.serializer(), it)}") }
    maxzoom?.let { properties.add("maxzoom: ${json.encodeToString(Double.serializer(), it)}") }
    colorRamp?.let { properties.add("colorRamp: ${it.identifier}") }
    property?.let { properties.add("property: ${json.encodeToString(String.serializer(), it)}") }
    weight?.let { properties.add("weight: ${json.encodeToString(MTNumberOrPropertyValuesSerializer, it)}") }
    radius?.let { properties.add("radius: ${json.encodeToString(MTRadiusOptionSerializer, it)}") }
    opacity?.let { properties.add("opacity: ${json.encodeToString(MTNumberOrZoomNumberValuesSerializer, it)}") }
    intensity?.let { properties.add("intensity: ${json.encodeToString(MTNumberOrZoomNumberValuesSerializer, it)}") }
    zoomCompensation?.let { properties.add("zoomCompensation: ${json.encodeToString(Boolean.serializer(), it)}") }

    return "{${properties.joinToString(separator = ",")}}"
}
