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
import com.maptiler.maptilersdk.helpers.MTPolygonLayerOptions
import com.maptiler.maptilersdk.helpers.MTStringOrZoomStringValuesSerializer
import kotlinx.serialization.builtins.serializer

internal data class AddPolygonLayer(
    val options: MTPolygonLayerOptions,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val optionsString: JSString = options.toJsObject()
        return "${MTBridge.SDK_OBJECT}.helpers.addPolygon(${MTBridge.MAP_OBJECT}, $optionsString);"
    }
}

private fun MTPolygonLayerOptions.toJsObject(): JSString {
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

    fillColor?.let {
        properties.add("fillColor: ${json.encodeToString(MTStringOrZoomStringValuesSerializer, it)}")
    }
    fillOpacity?.let {
        properties.add("fillOpacity: ${json.encodeToString(MTNumberOrZoomNumberValuesSerializer, it)}")
    }
    outlinePosition?.let {
        properties.add("outlinePosition: ${json.encodeToString(String.serializer(), it.jsName)}")
    }
    outlineDashArray?.let {
        properties.add("outlineDashArray: ${json.encodeToString(MTDashArrayOptionSerializer, it)}")
    }
    outlineCap?.let {
        properties.add("outlineCap: ${json.encodeToString(com.maptiler.maptilersdk.map.style.layer.line.MTLineCap.serializer(), it)}")
    }
    outlineJoin?.let {
        properties.add("outlineJoin: ${json.encodeToString(com.maptiler.maptilersdk.map.style.layer.line.MTLineJoin.serializer(), it)}")
    }
    pattern?.let { properties.add("pattern: ${json.encodeToString(String.serializer(), it)}") }
    outlineBlur?.let {
        properties.add("outlineBlur: ${json.encodeToString(MTNumberOrZoomNumberValuesSerializer, it)}")
    }

    return "{${properties.joinToString(separator = ",")}}"
}
