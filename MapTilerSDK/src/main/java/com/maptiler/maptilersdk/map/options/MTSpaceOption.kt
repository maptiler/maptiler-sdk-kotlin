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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

/**
 * Union wrapper for space option: can be a simple enable flag or a full config object.
 */
@Serializable(with = MTSpaceOptionSerializer::class)
sealed class MTSpaceOption {
    /** Enable space with default preset (SDK default). */
    data object Enabled : MTSpaceOption()

    /** Provide a full configuration. */
    data class Config(val value: MTSpace) : MTSpaceOption()
}

object MTSpaceOptionSerializer : KSerializer<MTSpaceOption> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("MTSpaceOption", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: MTSpaceOption,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: error("MTSpaceOptionSerializer only supports JSON encoding")
        when (value) {
            is MTSpaceOption.Enabled -> jsonEncoder.encodeJsonElement(JsonPrimitive(true))
            is MTSpaceOption.Config -> {
                val elem = Json.encodeToJsonElement(MTSpace.serializer(), value.value)
                jsonEncoder.encodeJsonElement(elem)
            }
        }
    }

    override fun deserialize(decoder: Decoder): MTSpaceOption {
        throw UnsupportedOperationException("Deserialization of MTSpaceOption is not supported")
    }
}
