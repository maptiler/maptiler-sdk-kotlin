/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.dsl

import com.maptiler.maptilersdk.helpers.toHexString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

/**
 * Unified style value that can be a constant or an expression (array).
 */
@Serializable(with = StyleValueSerializer::class)
sealed class StyleValue {
    data class Color(val value: Int) : StyleValue()

    data class Number(val value: Double) : StyleValue()

    data class Bool(val value: Boolean) : StyleValue()

    data class Str(val value: String) : StyleValue()

    data class Expression(val expr: PropertyValue) : StyleValue()
}

internal object StyleValueSerializer : KSerializer<StyleValue> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StyleValue", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: StyleValue,
    ) {
        if (encoder is JsonEncoder) {
            val element: JsonElement =
                when (value) {
                    is StyleValue.Color -> JsonPrimitive(value.value.toHexString())
                    is StyleValue.Number -> JsonPrimitive(value.value)
                    is StyleValue.Bool -> JsonPrimitive(value.value)
                    is StyleValue.Str -> JsonPrimitive(value.value)
                    is StyleValue.Expression -> value.expr.toJsonElement()
                }
            encoder.encodeJsonElement(element)
        } else {
            // Fallback: encode as string
            val text =
                when (value) {
                    is StyleValue.Color -> value.value.toHexString()
                    is StyleValue.Number -> value.value.toString()
                    is StyleValue.Bool -> if (value.value) "true" else "false"
                    is StyleValue.Str -> value.value
                    is StyleValue.Expression -> value.expr.asCode()
                }
            encoder.encodeString(text)
        }
    }

    override fun deserialize(decoder: Decoder): StyleValue {
        val text = decoder.decodeString()
        return StyleValue.Str(text)
    }
}

internal fun PropertyValue.toJsonElement(): JsonElement =
    when (this) {
        is PropertyValue.Str -> JsonPrimitive(value)
        is PropertyValue.Num -> JsonPrimitive(value)
        is PropertyValue.Bool -> JsonPrimitive(value)
        is PropertyValue.Arr -> JsonArray(values.map { it.toJsonElement() })
        is PropertyValue.RawJs -> JsonPrimitive(value)
        is PropertyValue.Color -> JsonPrimitive(argb.toHexString(includeAlpha))
    }
