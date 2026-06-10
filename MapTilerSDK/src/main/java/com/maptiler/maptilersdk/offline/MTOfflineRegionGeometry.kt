/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.map.LngLat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the geometry of an offline region.
 */
@Serializable
sealed class MTOfflineRegionGeometry {
    /**
     * The bounding box that contains the entire geometry.
     */
    abstract val bbox: MTBoundingBox

    /**
     * A rectangular bounding box.
     */
    @Serializable
    @SerialName("boundingBox")
    data class BoundingBox(override val bbox: MTBoundingBox) : MTOfflineRegionGeometry()

    /**
     * A route defined by a series of coordinates.
     */
    @Serializable
    @SerialName("route")
    data class Route(val coordinates: List<LngLat>) : MTOfflineRegionGeometry() {
        override val bbox: MTBoundingBox
            get() = MTBoundingBox.fromCoordinates(coordinates)
    }

    /**
     * A polygon defined by a series of coordinates (the boundary).
     */
    @Serializable
    @SerialName("polygon")
    data class Polygon(val coordinates: List<LngLat>) : MTOfflineRegionGeometry() {
        override val bbox: MTBoundingBox
            get() = MTBoundingBox.fromCoordinates(coordinates)
    }
}
