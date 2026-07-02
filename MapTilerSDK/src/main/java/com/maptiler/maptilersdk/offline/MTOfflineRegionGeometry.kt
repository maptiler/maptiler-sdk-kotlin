/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.helpers.LngLatListSerializer
import com.maptiler.maptilersdk.map.LngLat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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
    }

    /**
     * A polygon defined by a series of coordinates (the boundary).
     */
    @Serializable
    data class Polygon(val coordinates: List<LngLat>) : MTOfflineRegionGeometry() {
        override val bbox: MTBoundingBox
            get() = MTBoundingBox.fromCoordinates(coordinates)
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
