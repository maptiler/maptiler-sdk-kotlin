/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.source

import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTTileScheme
import java.net.URL

/**
 * A raster tile source. Tiles must be in ZXY or TMS tile format.
 * For raster tiles hosted by MapTiler, the URL should be of the form:
 * https://api.maptiler.com/tiles/[tilesetid]/tiles.json?key=<YOUR_MAPTILER_API_KEY>
 */
class MTRasterTileSource : MTTileSource {
    /** Unique identifier of a source. */
    override var identifier: String

    /** Attribution to be displayed when the map is shown to a user. */
    override var attribution: String? = null

    /**
     * An array containing the longitude and latitude of the southwest and northeast corners
     * of the source’s bounding box in the following order: [sw.lng, sw.lat, ne.lng, ne.lat].
     * Defaults to [-180, -85.051129, 180, 85.051129].
     */
    override var bounds: DoubleArray = doubleArrayOf(-180.0, -85.051129, 180.0, 85.051129)

    /** Maximum zoom level for which tiles are available. Defaults to 22. */
    override var maxZoom: Double = 22.0

    /** Minimum zoom level for which tiles are available. Defaults to 0. */
    override var minZoom: Double = 0.0

    /** An array of one or more tile source URLs. */
    override var tiles: Array<URL>? = null

    /** A URL to a TileJSON resource. Supported protocols are http, https. */
    override var url: URL? = null

    /** Type of the source. */
    override var type: MTSourceType = MTSourceType.RASTER
        private set

    /**
     * Scheme used for tiles. Influences the y direction of the tile coordinates.
     * Defaults to XYZ.
     */
    var scheme: MTTileScheme = MTTileScheme.XYZ

    /**
     * Tile size in pixels. Only configurable for raster sources. Defaults to 512.
     */
    var tileSize: Int = 512

    constructor(
        identifier: String,
        attribution: String?,
        bounds: DoubleArray,
        maxZoom: Double,
        minZoom: Double,
        tiles: Array<URL>?,
        url: URL?,
        type: MTSourceType,
        scheme: MTTileScheme,
        tileSize: Int,
    ) {
        this.identifier = identifier
        this.attribution = attribution
        this.bounds = bounds
        this.maxZoom = maxZoom
        this.minZoom = minZoom
        this.tiles = tiles
        this.url = url
        this.type = type
        this.scheme = scheme
        this.tileSize = tileSize
    }

    constructor(
        identifier: String,
        url: URL,
    ) {
        this.identifier = identifier
        this.url = url
    }

    /**
     * Sets the url of the source. Used for updating the source data.
     *
     * @param url URL to raster TileJSON resource.
     * @param mapViewController MTMapViewController which holds the source.
     */
    fun setURL(
        url: URL,
        mapViewController: MTMapViewController,
    ) {
        mapViewController.style?.setUrlToSource(url, this)
    }

    /**
     * Sets the tiles of the source. Used for updating the source data.
     *
     * @param tiles list of URLs with tile resources.
     * @param mapViewController MTMapViewController which holds the source.
     */
    fun setTiles(
        tiles: Array<URL>,
        mapViewController: MTMapViewController,
    ) {
        mapViewController.style?.setTilesToSource(tiles, this)
    }
}
