/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Union wrapper for halo option: can be a simple enable flag or a full config object.
 */
@Serializable(with = MTHaloOptionSerializer::class)
sealed class MTHaloOption {
    /** Enable halo with default gradient. */
    data object Enabled : MTHaloOption()

    /** Provide a full configuration. */
    data class Config(val value: MTHalo) : MTHaloOption()
}

object MTHaloOptionSerializer : KSerializer<MTHaloOption> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("MTHaloOption", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: MTHaloOption,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: error("MTHaloOptionSerializer only supports JSON encoding")
        when (value) {
            is MTHaloOption.Enabled -> jsonEncoder.encodeJsonElement(JsonPrimitive(true))
            is MTHaloOption.Config -> {
                jsonEncoder.encodeJsonElement(haloToJson(value.value))
            }
        }
    }

    override fun deserialize(decoder: Decoder): MTHaloOption {
        throw UnsupportedOperationException("Deserialization of MTHaloOption is not supported")
    }

    private fun haloToJson(halo: MTHalo): JsonObject {
        val content = mutableMapOf<String, kotlinx.serialization.json.JsonElement>()
        halo.scale?.let { content["scale"] = JsonPrimitive(it) }
        halo.stops?.let { stopsList ->
            val jsonStops =
                JsonArray(
                    stopsList.map { stop ->
                        JsonArray(listOf(JsonPrimitive(stop.position), JsonPrimitive(stop.color)))
                    },
                )
            content["stops"] = jsonStops
        }
        return JsonObject(content)
    }
}
