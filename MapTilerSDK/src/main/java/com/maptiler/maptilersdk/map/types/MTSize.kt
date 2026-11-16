/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.types

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

/**
 * Size represented by width and height in pixels.
 */
@Serializable(with = MTSizeSerializer::class)
data class MTSize(
    val width: Int,
    val height: Int,
)

internal object MTSizeSerializer : KSerializer<MTSize> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MTSize")

    override fun serialize(
        encoder: Encoder,
        value: MTSize,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: error("MTSizeSerializer can be used only with JSON")

        val serialized =
            buildJsonArray {
                add(JsonPrimitive(value.width))
                add(JsonPrimitive(value.height))
            }

        jsonEncoder.encodeJsonElement(serialized)
    }

    override fun deserialize(decoder: Decoder): MTSize {
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: error("MTSizeSerializer can be used only with JSON")
        val element = jsonDecoder.decodeJsonElement()

        return when (element) {
            is JsonArray -> decodeFromArray(element)
            else -> error("Unsupported size format: $element")
        }
    }

    private fun decodeFromArray(element: JsonArray): MTSize =
        when {
            element.size == 2 && element.all { it is JsonPrimitive } -> {
                val width = element[0].jsonPrimitive.int
                val height = element[1].jsonPrimitive.int
                MTSize(width, height)
            }
            else -> error("Unsupported size array format: $element")
        }
}
