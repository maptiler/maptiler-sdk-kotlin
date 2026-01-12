/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import androidx.annotation.ColorInt
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull

/**
 * Zoom/value pair for string-based ramps.
 */
@Serializable
data class MTZoomStringValue(val zoom: Double, val value: String)

typealias MTZoomStringValues = List<MTZoomStringValue>

/**
 * Zoom/value pair for number-based ramps.
 */
@Serializable
data class MTZoomNumberValue(val zoom: Double, val value: Double)

typealias MTZoomNumberValues = List<MTZoomNumberValue>

/**
 * Property/value pair for data-driven styles.
 */
@Serializable
data class MTHelperPropertyValue(val propertyValue: Double, val value: Double)

typealias MTPropertyValues = List<MTHelperPropertyValue>

/**
 * Color wrapper that always encodes as a hex string.
 * Accepts #RRGGBB or #RRGGBBAA (CSS hex). When constructed from a color int,
 * outputs #RRGGBB if opaque, otherwise #RRGGBBAA (alpha at the end).
 */
@Serializable(with = MTColorValueAsStringSerializer::class)
data class MTColorValue(val raw: String) {
    companion object {
        fun fromColorInt(
            @ColorInt color: Int,
        ): MTColorValue {
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = color and 0xFF
            val a = (color ushr 24) and 0xFF
            val hex =
                if (a == 0xFF) {
                    String.format("#%02X%02X%02X", r, g, b)
                } else {
                    String.format("#%02X%02X%02X%02X", r, g, b, a)
                }
            return MTColorValue(hex)
        }
    }
}

object MTColorValueAsStringSerializer : KSerializer<MTColorValue> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MTColorValue", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: MTColorValue,
    ) {
        encoder.encodeString(value.raw)
    }

    override fun deserialize(decoder: Decoder): MTColorValue = MTColorValue(decoder.decodeString())
}

/**
 * number | ZoomNumberValues
 */
@Serializable(with = MTNumberOrZoomNumberValuesSerializer::class)
sealed class MTNumberOrZoomNumberValues {
    data class Number(val value: Double) : MTNumberOrZoomNumberValues()

    data class Ramp(val values: MTZoomNumberValues) : MTNumberOrZoomNumberValues()
}

object MTNumberOrZoomNumberValuesSerializer : KSerializer<MTNumberOrZoomNumberValues> {
    private val listSer: SerializationStrategy<List<MTZoomNumberValue>> = ListSerializer(MTZoomNumberValue.serializer())
    private val listDeSer: DeserializationStrategy<List<MTZoomNumberValue>> = ListSerializer(MTZoomNumberValue.serializer())

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("MTNumberOrZoomNumberValues") {
            element<Double>("value")
            element<List<MTZoomNumberValue>>("values")
        }

    override fun serialize(
        encoder: Encoder,
        value: MTNumberOrZoomNumberValues,
    ) {
        when (value) {
            is MTNumberOrZoomNumberValues.Number -> encoder.encodeDouble(value.value)
            is MTNumberOrZoomNumberValues.Ramp -> (encoder as JsonEncoder).encodeSerializableValue(listSer, value.values)
        }
    }

    override fun deserialize(decoder: Decoder): MTNumberOrZoomNumberValues {
        val input = decoder as JsonDecoder
        val el = input.decodeJsonElement()
        return when (el) {
            is JsonPrimitive -> MTNumberOrZoomNumberValues.Number(el.double)
            is JsonArray -> MTNumberOrZoomNumberValues.Ramp(input.json.decodeFromJsonElement(listDeSer, el))
            else -> throw IllegalArgumentException("Unexpected JSON for NumberOrZoomNumberValues: $el")
        }
    }
}

/**
 * string | ZoomStringValues | Android color
 */
@Serializable(with = MTStringOrZoomStringValuesSerializer::class)
sealed class MTStringOrZoomStringValues {
    data class StringValue(val value: String) : MTStringOrZoomStringValues()

    data class Ramp(val values: MTZoomStringValues) : MTStringOrZoomStringValues()

    data class ColorHex(val color: MTColorValue) : MTStringOrZoomStringValues()

    companion object {
        fun fromColorInt(
            @ColorInt color: Int,
        ): MTStringOrZoomStringValues = ColorHex(MTColorValue.fromColorInt(color))
    }
}

object MTStringOrZoomStringValuesSerializer : KSerializer<MTStringOrZoomStringValues> {
    private val listSer: SerializationStrategy<List<MTZoomStringValue>> = ListSerializer(MTZoomStringValue.serializer())
    private val listDeSer: DeserializationStrategy<List<MTZoomStringValue>> = ListSerializer(MTZoomStringValue.serializer())

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("MTStringOrZoomStringValues") {
            element<String>("value")
            element<List<MTZoomStringValue>>("values")
        }

    override fun serialize(
        encoder: Encoder,
        value: MTStringOrZoomStringValues,
    ) {
        when (value) {
            is MTStringOrZoomStringValues.StringValue -> encoder.encodeString(value.value)
            is MTStringOrZoomStringValues.ColorHex -> encoder.encodeString(value.color.raw)
            is MTStringOrZoomStringValues.Ramp -> (encoder as JsonEncoder).encodeSerializableValue(listSer, value.values)
        }
    }

    override fun deserialize(decoder: Decoder): MTStringOrZoomStringValues {
        val input = decoder as JsonDecoder
        val el = input.decodeJsonElement()
        return when (el) {
            is JsonPrimitive -> MTStringOrZoomStringValues.StringValue(el.content)
            is JsonArray -> MTStringOrZoomStringValues.Ramp(input.json.decodeFromJsonElement(listDeSer, el))
            else -> throw IllegalArgumentException("Unexpected JSON for StringOrZoomStringValues: $el")
        }
    }
}

/**
 * number | PropertyValues
 */
@Serializable(with = MTNumberOrPropertyValuesSerializer::class)
sealed class MTNumberOrPropertyValues {
    data class Number(val value: Double) : MTNumberOrPropertyValues()

    data class PropertyMap(val values: MTPropertyValues) : MTNumberOrPropertyValues()
}

object MTNumberOrPropertyValuesSerializer : KSerializer<MTNumberOrPropertyValues> {
    private val listSer: SerializationStrategy<List<MTHelperPropertyValue>> = ListSerializer(MTHelperPropertyValue.serializer())
    private val listDeSer: DeserializationStrategy<List<MTHelperPropertyValue>> = ListSerializer(MTHelperPropertyValue.serializer())

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("MTNumberOrPropertyValues") {
            element<Double>("value")
            element<List<MTHelperPropertyValue>>("values")
        }

    override fun serialize(
        encoder: Encoder,
        value: MTNumberOrPropertyValues,
    ) {
        when (value) {
            is MTNumberOrPropertyValues.Number -> encoder.encodeDouble(value.value)
            is MTNumberOrPropertyValues.PropertyMap -> (encoder as JsonEncoder).encodeSerializableValue(listSer, value.values)
        }
    }

    override fun deserialize(decoder: Decoder): MTNumberOrPropertyValues {
        val input = decoder as JsonDecoder
        val el = input.decodeJsonElement()
        return when (el) {
            is JsonPrimitive -> MTNumberOrPropertyValues.Number(el.double)
            is JsonArray -> MTNumberOrPropertyValues.PropertyMap(input.json.decodeFromJsonElement(listDeSer, el))
            else -> throw IllegalArgumentException("Unexpected JSON for NumberOrPropertyValues: $el")
        }
    }
}

/**
 * number | ZoomNumberValues | PropertyValues
 */
@Serializable(with = MTRadiusOptionSerializer::class)
sealed class MTRadiusOption {
    data class Number(val value: Double) : MTRadiusOption()

    data class Zoom(val values: MTZoomNumberValues) : MTRadiusOption()

    data class Property(val values: MTPropertyValues) : MTRadiusOption()
}

object MTRadiusOptionSerializer : KSerializer<MTRadiusOption> {
    private val zoomSer: SerializationStrategy<List<MTZoomNumberValue>> = ListSerializer(MTZoomNumberValue.serializer())
    private val propSer: SerializationStrategy<List<MTHelperPropertyValue>> = ListSerializer(MTHelperPropertyValue.serializer())
    private val zoomDeSer: DeserializationStrategy<List<MTZoomNumberValue>> = ListSerializer(MTZoomNumberValue.serializer())
    private val propDeSer: DeserializationStrategy<List<MTHelperPropertyValue>> = ListSerializer(MTHelperPropertyValue.serializer())

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("MTRadiusOption") {
            element<Double>("value")
            element<List<MTZoomNumberValue>>("zoom")
            element<List<MTHelperPropertyValue>>("property")
        }

    override fun serialize(
        encoder: Encoder,
        value: MTRadiusOption,
    ) {
        when (value) {
            is MTRadiusOption.Number -> encoder.encodeDouble(value.value)
            is MTRadiusOption.Zoom -> (encoder as JsonEncoder).encodeSerializableValue(zoomSer, value.values)
            is MTRadiusOption.Property -> (encoder as JsonEncoder).encodeSerializableValue(propSer, value.values)
        }
    }

    override fun deserialize(decoder: Decoder): MTRadiusOption {
        val input = decoder as JsonDecoder
        val el = input.decodeJsonElement()
        return when (el) {
            is JsonPrimitive -> MTRadiusOption.Number(el.double)
            is JsonArray -> {
                // Peek first element to distinguish shapes
                val first: JsonElement? = el.firstOrNull()
                if (first is JsonObject) {
                    val hasZoom = first["zoom"] != null
                    val hasProp = first["propertyValue"] != null
                    when {
                        hasZoom -> MTRadiusOption.Zoom(input.json.decodeFromJsonElement(zoomDeSer, el))
                        hasProp -> MTRadiusOption.Property(input.json.decodeFromJsonElement(propDeSer, el))
                        else -> throw IllegalArgumentException("Cannot infer RadiusOption from: $el")
                    }
                } else {
                    throw IllegalArgumentException("Unexpected array element for RadiusOption: $el")
                }
            }
            else -> throw IllegalArgumentException("Unexpected JSON for RadiusOption: $el")
        }
    }
}

private val JsonPrimitive.double: Double
    get() = this.doubleOrNull ?: this.content.toDouble()
