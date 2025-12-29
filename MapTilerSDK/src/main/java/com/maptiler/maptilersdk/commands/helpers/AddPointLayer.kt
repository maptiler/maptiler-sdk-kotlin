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
import com.maptiler.maptilersdk.helpers.MTPointLayerOptions
import kotlinx.serialization.builtins.serializer

internal data class AddPointLayer(
    val options: MTPointLayerOptions,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val optionsString: JSString = options.toJsObject()
        return "${MTBridge.SDK_OBJECT}.helpers.addPoint(${MTBridge.MAP_OBJECT}, $optionsString);"
    }
}

private fun MTPointLayerOptions.toJsObject(): JSString {
    val json = JsonConfig.json
    val properties = mutableListOf<String>()

    properties.add("data: ${json.encodeToString(String.serializer(), data)}")
    layerId?.let { properties.add("layerId: ${json.encodeToString(String.serializer(), it)}") }
    sourceId?.let { properties.add("sourceId: ${json.encodeToString(String.serializer(), it)}") }
    beforeId?.let { properties.add("beforeId: ${json.encodeToString(String.serializer(), it)}") }
    minzoom?.let { properties.add("minzoom: ${json.encodeToString(Double.serializer(), it)}") }
    maxzoom?.let { properties.add("maxzoom: ${json.encodeToString(Double.serializer(), it)}") }
    outline?.let { properties.add("outline: ${json.encodeToString(Boolean.serializer(), it)}") }
    outlineColor?.let { properties.add("outlineColor: ${json.encodeToString(String.serializer(), it)}") }
    outlineWidth?.let { properties.add("outlineWidth: ${json.encodeToString(Double.serializer(), it)}") }
    outlineOpacity?.let { properties.add("outlineOpacity: ${json.encodeToString(Double.serializer(), it)}") }
    if (pointColorRamp != null) {
        properties.add("pointColor: ${pointColorRamp.identifier}")
    } else {
        pointColor?.let { properties.add("pointColor: ${json.encodeToString(String.serializer(), it)}") }
    }
    pointRadius?.let { properties.add("pointRadius: ${json.encodeToString(Double.serializer(), it)}") }
    minPointRadius?.let { properties.add("minPointRadius: ${json.encodeToString(Double.serializer(), it)}") }
    maxPointRadius?.let { properties.add("maxPointRadius: ${json.encodeToString(Double.serializer(), it)}") }
    property?.let { properties.add("property: ${json.encodeToString(String.serializer(), it)}") }
    pointOpacity?.let { properties.add("pointOpacity: ${json.encodeToString(Double.serializer(), it)}") }
    alignOnViewport?.let { properties.add("alignOnViewport: ${json.encodeToString(Boolean.serializer(), it)}") }
    cluster?.let { properties.add("cluster: ${json.encodeToString(Boolean.serializer(), it)}") }
    showLabel?.let { properties.add("showLabel: ${json.encodeToString(Boolean.serializer(), it)}") }
    labelColor?.let { properties.add("labelColor: ${json.encodeToString(String.serializer(), it)}") }
    labelSize?.let { properties.add("labelSize: ${json.encodeToString(Double.serializer(), it)}") }
    zoomCompensation?.let { properties.add("zoomCompensation: ${json.encodeToString(Boolean.serializer(), it)}") }

    return "{${properties.joinToString(separator = ",")}}"
}
