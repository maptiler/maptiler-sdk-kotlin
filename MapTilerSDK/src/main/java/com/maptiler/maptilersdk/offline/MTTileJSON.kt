/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.map.style.MTTileScheme
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a TileJSON metadata object.
 */
@Serializable
internal data class MTTileJSON(
    /**
     * An array of tile endpoints.
     */
    val tiles: List<String>,
    /**
     * The coordinate scheme used. Default is "xyz".
     */
    val scheme: MTTileScheme = MTTileScheme.XYZ,
    /**
     * The bounds of the map [west, south, east, north].
     */
    val bounds: List<Double>? = null,
    /**
     * An integer specifying the minimum zoom level. Default is 0.
     */
    val minzoom: Int = 0,
    /**
     * An integer specifying the maximum zoom level. Default is 22.
     */
    val maxzoom: Int = 22,
    /**
     * The size of the tiles in pixels. Default is 256.
     */
    @SerialName("tileSize")
    val tileSizeAlt1: Int? = null,
    @SerialName("tile_size")
    val tileSizeAlt2: Int? = null,
    @SerialName("tilesize")
    val tileSizeAlt3: Int? = null,
) {
    /**
     * The resolved tile size.
     */
    val tileSize: Int
        get() = tileSizeAlt1 ?: tileSizeAlt2 ?: tileSizeAlt3 ?: 256

    /**
     * The preferred tile URL template, prioritizing `https://` if available.
     */
    val preferredTileUrlTemplate: String?
        get() = tiles.firstOrNull { it.startsWith("https://") } ?: tiles.firstOrNull()
}
