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
import com.maptiler.maptilersdk.helpers.MTDashArrayOptionSerializer
import com.maptiler.maptilersdk.helpers.MTNumberOrZoomNumberValuesSerializer
import com.maptiler.maptilersdk.helpers.MTPolylineLayerOptions
import com.maptiler.maptilersdk.helpers.MTStringOrZoomStringValuesSerializer
import com.maptiler.maptilersdk.map.style.layer.line.MTLineCap
import com.maptiler.maptilersdk.map.style.layer.line.MTLineJoin
import kotlinx.serialization.builtins.serializer

internal data class AddPolylineLayer(
    val options: MTPolylineLayerOptions,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val optionsString: JSString = options.toJsObject()
        return "${MTBridge.SDK_OBJECT}.helpers.addPolyline(${MTBridge.MAP_OBJECT}, $optionsString);"
    }
}

private fun MTPolylineLayerOptions.toJsObject(): JSString {
    val json = JsonConfig.json
    val properties = mutableListOf<String>()

    properties.add("data: ${json.encodeToString(String.serializer(), data)}")
    layerId?.let { properties.add("layerId: ${json.encodeToString(String.serializer(), it)}") }
    sourceId?.let { properties.add("sourceId: ${json.encodeToString(String.serializer(), it)}") }
    beforeId?.let { properties.add("beforeId: ${json.encodeToString(String.serializer(), it)}") }
    minzoom?.let { properties.add("minzoom: ${json.encodeToString(Double.serializer(), it)}") }
    maxzoom?.let { properties.add("maxzoom: ${json.encodeToString(Double.serializer(), it)}") }
    outline?.let { properties.add("outline: ${json.encodeToString(Boolean.serializer(), it)}") }
    outlineColor?.let {
        properties.add("outlineColor: ${json.encodeToString(MTStringOrZoomStringValuesSerializer, it)}")
    }
    outlineWidth?.let {
        properties.add("outlineWidth: ${json.encodeToString(MTNumberOrZoomNumberValuesSerializer, it)}")
    }
    outlineOpacity?.let {
        properties.add("outlineOpacity: ${json.encodeToString(MTNumberOrZoomNumberValuesSerializer, it)}")
    }
    outlineBlur?.let {
        properties.add("outlineBlur: ${json.encodeToString(MTNumberOrZoomNumberValuesSerializer, it)}")
    }

    lineColor?.let {
        properties.add("lineColor: ${json.encodeToString(MTStringOrZoomStringValuesSerializer, it)}")
    }
    lineWidth?.let { properties.add("lineWidth: ${json.encodeToString(MTNumberOrZoomNumberValuesSerializer, it)}") }
    lineOpacity?.let { properties.add("lineOpacity: ${json.encodeToString(MTNumberOrZoomNumberValuesSerializer, it)}") }
    lineBlur?.let { properties.add("lineBlur: ${json.encodeToString(MTNumberOrZoomNumberValuesSerializer, it)}") }
    lineGapWidth?.let { properties.add("lineGapWidth: ${json.encodeToString(MTNumberOrZoomNumberValuesSerializer, it)}") }
    lineDashArray?.let { properties.add("lineDashArray: ${json.encodeToString(MTDashArrayOptionSerializer, it)}") }
    lineCap?.let { properties.add("lineCap: ${json.encodeToString(MTLineCap.serializer(), it)}") }
    lineJoin?.let { properties.add("lineJoin: ${json.encodeToString(MTLineJoin.serializer(), it)}") }

    return "{${properties.joinToString(separator = ",")}}"
}
