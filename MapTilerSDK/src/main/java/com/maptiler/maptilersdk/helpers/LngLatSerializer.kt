/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import com.maptiler.maptilersdk.map.LngLat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializes [LngLat] as a [longitude, latitude] array.
 */
object LngLatArraySerializer : KSerializer<LngLat> {
    private val delegate = ListSerializer(Double.serializer())
    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: LngLat,
    ) {
        encoder.encodeSerializableValue(delegate, listOf(value.lng, value.lat))
    }

    override fun deserialize(decoder: Decoder): LngLat {
        val list = decoder.decodeSerializableValue(delegate)
        return LngLat(lng = list[0], lat = list[1])
    }
}

/**
 * Serializes a list of [LngLat] as a list of [longitude, latitude] arrays.
 */
object LngLatListSerializer : KSerializer<List<LngLat>> {
    private val delegate = ListSerializer(LngLatArraySerializer)
    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: List<LngLat>,
    ) {
        encoder.encodeSerializableValue(delegate, value)
    }

    override fun deserialize(decoder: Decoder): List<LngLat> {
        return decoder.decodeSerializableValue(delegate)
    }
}
