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
import com.maptiler.maptilersdk.helpers.MTHeatmapValue
import com.maptiler.maptilersdk.helpers.MTPropertyValueStop
import com.maptiler.maptilersdk.helpers.MTZoomNumberValue
import kotlinx.serialization.builtins.ListSerializer
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
    weight?.let { encodeHeatmapValue("weight", it)?.let(properties::add) }
    radius?.let { encodeHeatmapValue("radius", it)?.let(properties::add) }
    opacity?.let { encodeHeatmapValue("opacity", it)?.let(properties::add) }
    intensity?.let { encodeHeatmapValue("intensity", it)?.let(properties::add) }
    zoomCompensation?.let { properties.add("zoomCompensation: ${json.encodeToString(Boolean.serializer(), it)}") }

    return "{${properties.joinToString(separator = ",")}}"
}

private fun encodeHeatmapValue(
    name: String,
    value: MTHeatmapValue,
): String? {
    val json = JsonConfig.json
    val encoded =
        when (value) {
            is MTHeatmapValue.Constant -> json.encodeToString(Double.serializer(), value.value)
            is MTHeatmapValue.ZoomValues ->
                json.encodeToString(
                    ListSerializer(MTZoomNumberValue.serializer()),
                    value.values,
                )
            is MTHeatmapValue.PropertyValues ->
                json.encodeToString(
                    ListSerializer(MTPropertyValueStop.serializer()),
                    value.values,
                )
        }
    return "$name: $encoded"
}
