/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.helpers.LngLatListSerializer
import com.maptiler.maptilersdk.map.LngLat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Represents the geometry of an offline region.
 */
@Serializable(with = MTOfflineRegionGeometrySerializer::class)
sealed class MTOfflineRegionGeometry {
    /**
     * The bounding box that contains the entire geometry.
     */
    abstract val bbox: MTBoundingBox

    /**
     * A rectangular bounding box.
     */
    @Serializable
    data class BoundingBox(override val bbox: MTBoundingBox) : MTOfflineRegionGeometry()

    /**
     * A route defined by a series of coordinates.
     */
    @Serializable
    data class Route(val coordinates: List<LngLat>) : MTOfflineRegionGeometry() {
        override val bbox: MTBoundingBox
            get() = MTBoundingBox.fromCoordinates(coordinates)

        companion object {
            /**
             * Creates a [Route] from a GeoJSON LineString string.
             *
             * @param json The GeoJSON string.
             * @return A new [Route] instance.
             */
            fun fromGeoJson(json: String): Route {
                val element = JsonConfig.json.parseToJsonElement(json)
                return fromJsonElement(element)
            }

            internal fun fromJsonElement(element: JsonElement): Route {
                val obj = element.jsonObject
                val type = obj["type"]?.jsonPrimitive?.content

                return when (type) {
                    "LineString" -> {
                        val coords =
                            obj["coordinates"]?.jsonArray?.map {
                                val arr = it.jsonArray
                                LngLat(arr[0].jsonPrimitive.double, arr[1].jsonPrimitive.double)
                            } ?: throw IllegalArgumentException("Missing coordinates")
                        Route(coords)
                    }

                    "Feature" -> {
                        val geometry = obj["geometry"] ?: throw IllegalArgumentException("Missing geometry")
                        fromJsonElement(geometry)
                    }

                    "FeatureCollection" -> {
                        val features = obj["features"]?.jsonArray ?: throw IllegalArgumentException("Missing features")
                        // Take the first LineString found
                        for (feature in features) {
                            try {
                                return fromJsonElement(feature)
                            } catch (e: Exception) {
                                continue
                            }
                        }
                        throw IllegalArgumentException("No LineString found in FeatureCollection")
                    }

                    else -> throw IllegalArgumentException("Unsupported GeoJSON type for Route: $type")
                }
            }
        }
    }

    /**
     * A polygon defined by a series of coordinates (the boundary).
     */
    @Serializable
    data class Polygon(val coordinates: List<LngLat>) : MTOfflineRegionGeometry() {
        override val bbox: MTBoundingBox
            get() = MTBoundingBox.fromCoordinates(coordinates)

        companion object {
            /**
             * Creates a [Polygon] from a GeoJSON Polygon string.
             *
             * @param json The GeoJSON string.
             * @return A new [Polygon] instance.
             */
            fun fromGeoJson(json: String): Polygon {
                val element = JsonConfig.json.parseToJsonElement(json)
                return fromJsonElement(element)
            }

            internal fun fromJsonElement(element: JsonElement): Polygon {
                val obj = element.jsonObject
                val type = obj["type"]?.jsonPrimitive?.content

                return when (type) {
                    "Polygon" -> {
                        val rings = obj["coordinates"]?.jsonArray ?: throw IllegalArgumentException("Missing coordinates")
                        // Take the outer ring (first element)
                        val coords =
                            rings[0].jsonArray.map {
                                val arr = it.jsonArray
                                LngLat(arr[0].jsonPrimitive.double, arr[1].jsonPrimitive.double)
                            }
                        Polygon(coords)
                    }

                    "Feature" -> {
                        val geometry = obj["geometry"] ?: throw IllegalArgumentException("Missing geometry")
                        fromJsonElement(geometry)
                    }

                    "FeatureCollection" -> {
                        val features = obj["features"]?.jsonArray ?: throw IllegalArgumentException("Missing features")
                        // Take the first Polygon found
                        for (feature in features) {
                            try {
                                return fromJsonElement(feature)
                            } catch (e: Exception) {
                                continue
                            }
                        }
                        throw IllegalArgumentException("No Polygon found in FeatureCollection")
                    }

                    else -> throw IllegalArgumentException("Unsupported GeoJSON type for Polygon: $type")
                }
            }
        }
    }
}

internal object MTOfflineRegionGeometrySerializer : KSerializer<MTOfflineRegionGeometry> {
    override val descriptor: SerialDescriptor = GeometrySurrogate.serializer().descriptor

    override fun serialize(
        encoder: Encoder,
        value: MTOfflineRegionGeometry,
    ) {
        val surrogate =
            when (value) {
                is MTOfflineRegionGeometry.BoundingBox -> GeometrySurrogate(boundingBox = value.bbox)
                is MTOfflineRegionGeometry.Route -> GeometrySurrogate(route = value.coordinates)
                is MTOfflineRegionGeometry.Polygon -> GeometrySurrogate(polygon = value.coordinates)
            }
        encoder.encodeSerializableValue(GeometrySurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): MTOfflineRegionGeometry {
        val surrogate = decoder.decodeSerializableValue(GeometrySurrogate.serializer())
        return when {
            surrogate.boundingBox != null -> MTOfflineRegionGeometry.BoundingBox(surrogate.boundingBox)
            surrogate.route != null -> MTOfflineRegionGeometry.Route(surrogate.route)
            surrogate.polygon != null -> MTOfflineRegionGeometry.Polygon(surrogate.polygon)
            else -> throw IllegalArgumentException("MTOfflineRegionGeometry must have one of 'boundingBox', 'route', or 'polygon'")
        }
    }

    @Serializable
    private data class GeometrySurrogate(
        val boundingBox: MTBoundingBox? = null,
        @Serializable(with = LngLatListSerializer::class)
        val route: List<LngLat>? = null,
        @Serializable(with = LngLatListSerializer::class)
        val polygon: List<LngLat>? = null,
    )
}
