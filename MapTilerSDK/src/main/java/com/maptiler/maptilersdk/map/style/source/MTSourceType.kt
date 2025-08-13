/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.source

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Types of sources.
 *
 * Sources state which data the map should display.
 */
@Serializable
enum class MTSourceType {
    /**
     * Vector tile source.
     */
    @SerialName("vector")
    VECTOR,

    /**
     * Raster tile source.
     */
    @SerialName("raster")
    RASTER,

    /**
     * Raster DEM source.
     */
    @SerialName("raster-dem")
    RASTER_DEM,

    /**
     * GeoJSON source.
     */
    @SerialName("geojson")
    GEOJSON,

    /**
     * Image source.
     */
    @SerialName("image")
    IMAGE,

    /**
     * Video source.
     */
    @SerialName("video")
    VIDEO,
}
