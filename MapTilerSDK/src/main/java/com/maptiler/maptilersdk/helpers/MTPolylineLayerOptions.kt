/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import com.maptiler.maptilersdk.map.style.layer.line.MTLineCap
import com.maptiler.maptilersdk.map.style.layer.line.MTLineJoin
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.element

/**
 * Options for building a polyline visualization layer through the helper.
 */
@Serializable
@Suppress("LongParameterList")
data class MTPolylineLayerOptions(
    val data: String,
    val layerId: String? = null,
    val sourceId: String? = null,
    val beforeId: String? = null,
    val minzoom: Double? = null,
    val maxzoom: Double? = null,
    val outline: Boolean? = null,
    val outlineColor: MTStringOrZoomStringValues? = null,
    val outlineWidth: MTNumberOrZoomNumberValues? = null,
    val outlineOpacity: MTNumberOrZoomNumberValues? = null,
    // Polyline specific
    val lineColor: MTStringOrZoomStringValues? = null,
    val lineWidth: MTNumberOrZoomNumberValues? = null,
    val lineOpacity: MTNumberOrZoomNumberValues? = null,
    val lineBlur: MTNumberOrZoomNumberValues? = null,
    val lineGapWidth: MTNumberOrZoomNumberValues? = null,
    val lineDashArray: MTDashArrayOption? = null,
    val lineCap: MTLineCap? = null,
    val lineJoin: MTLineJoin? = null,
    val outlineBlur: MTNumberOrZoomNumberValues? = null,
) {
    // Convenience constructor for simpler types (String colors and Double numerics, dash pattern as string)
    constructor(
        data: String,
        layerId: String? = null,
        sourceId: String? = null,
        beforeId: String? = null,
        minzoom: Double? = null,
        maxzoom: Double? = null,
        outline: Boolean? = null,
        outlineColor: String? = null,
        outlineWidth: Double? = null,
        outlineOpacity: Double? = null,
        lineColor: String? = null,
        lineWidth: Double? = null,
        lineOpacity: Double? = null,
        lineBlur: Double? = null,
        lineGapWidth: Double? = null,
        lineDashPattern: String? = null,
        lineCap: String? = null,
        lineJoin: String? = null,
        outlineBlur: Double? = null,
    ) : this(
        data = data,
        layerId = layerId,
        sourceId = sourceId,
        beforeId = beforeId,
        minzoom = minzoom,
        maxzoom = maxzoom,
        outline = outline,
        outlineColor = outlineColor?.let { MTStringOrZoomStringValues.StringValue(it) },
        outlineWidth = outlineWidth?.let { MTNumberOrZoomNumberValues.Number(it) },
        outlineOpacity = outlineOpacity?.let { MTNumberOrZoomNumberValues.Number(it) },
        lineColor = lineColor?.let { MTStringOrZoomStringValues.StringValue(it) },
        lineWidth = lineWidth?.let { MTNumberOrZoomNumberValues.Number(it) },
        lineOpacity = lineOpacity?.let { MTNumberOrZoomNumberValues.Number(it) },
        lineBlur = lineBlur?.let { MTNumberOrZoomNumberValues.Number(it) },
        lineGapWidth = lineGapWidth?.let { MTNumberOrZoomNumberValues.Number(it) },
        lineDashArray = lineDashPattern?.let { MTDashArrayOption.StringValue(it) },
        lineCap = lineCap?.let { it.toMTLineCapOrNull() },
        lineJoin = lineJoin?.let { it.toMTLineJoinOrNull() },
        outlineBlur = outlineBlur?.let { MTNumberOrZoomNumberValues.Number(it) },
    )
}

private fun String.toMTLineCapOrNull(): MTLineCap? =
    when (lowercase()) {
        "butt" -> MTLineCap.BUTT
        "round" -> MTLineCap.ROUND
        "square" -> MTLineCap.SQUARE
        else -> null
    }

private fun String.toMTLineJoinOrNull(): MTLineJoin? =
    when (lowercase()) {
        "bevel" -> MTLineJoin.BEVEL
        "round" -> MTLineJoin.ROUND
        "miter" -> MTLineJoin.MITER
        else -> null
    }

/**
 * Dash array option accepting either an array of numbers or the underscore/space pattern string.
 */
@Serializable(with = MTDashArrayOptionSerializer::class)
sealed class MTDashArrayOption {
    data class Numbers(val values: List<Double>) : MTDashArrayOption()

    data class StringValue(val pattern: String) : MTDashArrayOption()
}

object MTDashArrayOptionSerializer : kotlinx.serialization.KSerializer<MTDashArrayOption> {
    override val descriptor: kotlinx.serialization.descriptors.SerialDescriptor =
        kotlinx.serialization.descriptors.buildClassSerialDescriptor("MTDashArrayOption") {
            element<List<Double>>("values")
            element<String>("pattern")
        }

    override fun serialize(
        encoder: kotlinx.serialization.encoding.Encoder,
        value: MTDashArrayOption,
    ) {
        when (value) {
            is MTDashArrayOption.Numbers ->
                (encoder as kotlinx.serialization.json.JsonEncoder)
                    .encodeSerializableValue(kotlinx.serialization.builtins.ListSerializer(Double.serializer()), value.values)
            is MTDashArrayOption.StringValue -> encoder.encodeString(value.pattern)
        }
    }

    override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder): MTDashArrayOption {
        val input = decoder as kotlinx.serialization.json.JsonDecoder
        val el = input.decodeJsonElement()
        return when (el) {
            is kotlinx.serialization.json.JsonArray ->
                MTDashArrayOption.Numbers(
                    input.json.decodeFromJsonElement(
                        kotlinx.serialization.builtins.ListSerializer(Double.serializer()),
                        el,
                    ),
                )
            is kotlinx.serialization.json.JsonPrimitive -> MTDashArrayOption.StringValue(el.content)
            else -> throw IllegalArgumentException("Unexpected JSON for MTDashArrayOption: $el")
        }
    }
}
