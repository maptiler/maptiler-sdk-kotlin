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
 * A vector tile source.
 */
class MTVectorTileSource : MTTileSource {
    /**
     * Unique identifier of a source.
     */
    override var identifier: String

    /**
     * Attribution to be displayed when the map is shown to a user.
     */
    override var attribution: String? = null

    /**
     * An array containing the longitude and latitude of the southwest and northeast corners
     * of the source’s bounding box in the following order: [sw.lng, sw.lat, ne.lng, ne.lat].
     *
     * Defaults to [-180, -85.051129, 180, 85.051129].
     */
    override var bounds: DoubleArray = doubleArrayOf(-180.0, -85.051129, 180.0, 85.051129)

    /**
     * Maximum zoom level for which tiles are available.
     *
     * Defaults to 22.
     */
    override var maxZoom: Double = 22.0

    /**
     * Minimum zoom level for which tiles are available.
     *
     * Defaults to 0.
     */
    override var minZoom: Double = 0.0

    /**
     * An array of one or more tile source URLs.
     */
    override var tiles: Array<URL>? = null

    /**
     * A URL to a TileJSON resource. Supported protocols are http, https.
     */
    override var url: URL?

    /**
     * Type of the layer.
     */
    override var type: MTSourceType = MTSourceType.VECTOR

    /**
     * Scheme used for tiles.
     * Influences the y direction of the tile coordinates. The global-mercator (aka Spherical Mercator) is assumed.
     */
    var scheme: MTTileScheme = MTTileScheme.XYZ

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
    }

    constructor(
        identifier: String,
        url: URL,
    ) {
        this.identifier = identifier
        this.url = url
    }

    /**
     * Sets the url of the source.
     *
     * Used for updating the source data.
     *
     * @param url url to Vector Tile resource.
     * @param mapViewController MTMapViewController which holds the source.
     */
    fun setURL(
        url: URL,
        mapViewController: MTMapViewController,
    ) {
        mapViewController.style?.setUrlToSource(url, this)
    }

    /**
     * Sets the tiles of the source.
     *
     * Used for updating the source data.
     *
     * @param tiles list of urls with tile resources.
     * @param mapViewController MTMapViewController which holds the source.
     */
    fun setTiles(
        tiles: Array<URL>,
        mapViewController: MTMapViewController,
    ) {
        mapViewController.style?.setTilesToSource(tiles, this)
    }
}
