/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Types of layers.
 */
@Serializable
enum class MTLayerType {
    /**
     * A filled polygon with an optional stroked border.
     */
    FILL,

    /**
     * A stroked line.
     */
    LINE,

    /**
     * An icon or a text label.
     */
    SYMBOL,

    /**
     * Raster map textures such as satellite imagery.
     */
    RASTER,

    /**
     * A filled circle.
     */
    CIRCLE,

    /**
     * An extruded (3D) polygon.
     */
    @SerialName("fill-extrusion")
    FILL_EXTRUSION,

    /**
     * A heatmap.
     */
    HEATMAP,

    /**
     * Client-side hillshading visualization based on DEM data.
     */
    HILLSHADE,

    /**
     * The background color or pattern of the map.
     */
    BACKGROUND,
}
