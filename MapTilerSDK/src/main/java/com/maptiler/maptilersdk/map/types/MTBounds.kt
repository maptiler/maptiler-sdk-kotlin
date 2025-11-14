/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.types

import com.maptiler.maptilersdk.map.LngLat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Geographical bounding box represented by south-west and north-east corners.
 *
 * The bounds are serialized for the JavaScript bridge as `[[west, south], [east, north]]`,
 * which matches the `LngLatBoundsLike` structure accepted by the MapTiler SDK for JS.
 */
@Serializable(with = MTBoundsSerializer::class)
data class MTBounds(
    val southwest: LngLat,
    val northeast: LngLat,
) {
    /**
     * Convenience constructor accepting west, south, east and north edges in degrees.
     */
    constructor(
        west: Double,
        south: Double,
        east: Double,
        north: Double,
    ) : this(
        southwest = LngLat(west, south),
        northeast = LngLat(east, north),
    )
}

internal object MTBoundsSerializer : KSerializer<MTBounds> {
    private const val SOUTHWEST_KEY = "_sw"
    private const val NORTHEAST_KEY = "_ne"
    private const val SOUTHWEST_ALT_KEY = "southwest"
    private const val NORTHEAST_ALT_KEY = "northeast"

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MTBounds")

    override fun serialize(
        encoder: Encoder,
        value: MTBounds,
    ) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: error("MTBoundsSerializer can be used only with JSON")

        val southwest = value.southwest
        val northeast = value.northeast

        val serialized =
            buildJsonArray {
                add(
                    buildJsonArray {
                        add(JsonPrimitive(southwest.lng))
                        add(JsonPrimitive(southwest.lat))
                    },
                )
                add(
                    buildJsonArray {
                        add(JsonPrimitive(northeast.lng))
                        add(JsonPrimitive(northeast.lat))
                    },
                )
            }

        jsonEncoder.encodeJsonElement(serialized)
    }

    override fun deserialize(decoder: Decoder): MTBounds {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("MTBoundsSerializer can be used only with JSON")
        val element = jsonDecoder.decodeJsonElement()

        return when (element) {
            is JsonArray -> decodeFromArray(element)
            is JsonObject -> decodeFromObject(element, jsonDecoder)
            else -> error("Unsupported bounds format: $element")
        }
    }

    private fun decodeFromArray(element: JsonArray): MTBounds =
        when {
            element.size == 4 && element.all { it is JsonPrimitive } -> {
                val west = element[0].jsonPrimitive.double
                val south = element[1].jsonPrimitive.double
                val east = element[2].jsonPrimitive.double
                val north = element[3].jsonPrimitive.double
                MTBounds(west, south, east, north)
            }
            element.size == 2 &&
                element[0] is JsonArray &&
                element[1] is JsonArray &&
                element[0].jsonArray.size == 2 &&
                element[1].jsonArray.size == 2 -> {
                val swArray = element[0].jsonArray
                val neArray = element[1].jsonArray
                val southwest = LngLat(swArray[0].jsonPrimitive.double, swArray[1].jsonPrimitive.double)
                val northeast = LngLat(neArray[0].jsonPrimitive.double, neArray[1].jsonPrimitive.double)
                MTBounds(southwest, northeast)
            }
            else -> error("Unsupported bounds array format: $element")
        }

    private fun decodeFromObject(
        element: JsonObject,
        jsonDecoder: JsonDecoder,
    ): MTBounds {
        val json = jsonDecoder.json
        val southwestElement =
            element[SOUTHWEST_KEY]
                ?: element[SOUTHWEST_ALT_KEY]
                ?: error("Bounds object missing southwest definition: $element")
        val northeastElement =
            element[NORTHEAST_KEY]
                ?: element[NORTHEAST_ALT_KEY]
                ?: error("Bounds object missing northeast definition: $element")

        val southwest = json.decodeFromJsonElement(LngLat.serializer(), southwestElement)
        val northeast = json.decodeFromJsonElement(LngLat.serializer(), northeastElement)

        return MTBounds(southwest, northeast)
    }
}
