/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.source

import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapViewController
import java.net.URL

/**
 * An image source.
 *
 * The url value contains the image location. The coordinates array contains [longitude, latitude] pairs for the
 * image corners listed in clockwise order: top left, top right, bottom right, bottom left.
 */
class MTImageSource(
    /** Unique identifier of a source. */
    override var identifier: String,
    /** URL pointing to the image. */
    url: URL,
    /** Corners of the image in clockwise order starting at top-left. */
    var coordinates: List<LngLat>,
) : MTSource {
    override var url: URL? = url

    init {
        require(coordinates.size == 4) { "Image source requires exactly 4 corner coordinates" }
    }

    /** Type of the source. */
    override val type: MTSourceType = MTSourceType.IMAGE

    /**
     * Updates the image corners.
     *
     * @param coordinates Four coordinates in clockwise order starting at top-left.
     * @param mapViewController Map controller that owns the style.
     */
    fun setCoordinates(
        coordinates: List<LngLat>,
        mapViewController: MTMapViewController,
    ) {
        require(coordinates.size == 4) { "Image source requires exactly 4 corner coordinates" }
        this.coordinates = coordinates
        mapViewController.style?.setCoordinatesToImageSource(coordinates, this)
    }

    /**
     * Updates both the image URL and its corner coordinates atomically.
     *
     * @param url New image URL.
     * @param coordinates Four coordinates in clockwise order starting at top-left.
     * @param mapViewController Map controller that owns the style.
     */
    fun updateImage(
        url: URL,
        coordinates: List<LngLat>,
        mapViewController: MTMapViewController,
    ) {
        require(coordinates.size == 4) { "Image source requires exactly 4 corner coordinates" }
        this.url = url
        this.coordinates = coordinates
        mapViewController.style?.updateImageSource(url, coordinates, this)
    }
}
