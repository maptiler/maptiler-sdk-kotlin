/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.source

import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapViewController
import java.net.URL

/**
 * A video source.
 *
 * The urls value contains the video locations in preferred order.
 * The coordinates array contains [longitude, latitude] pairs for the video corners listed in clockwise order:
 * top left, top right, bottom right, bottom left.
 */
class MTVideoSource(
    /** Unique identifier of a source. */
    override var identifier: String,
    /** URLs pointing to the video in preferred order (format variants). */
    var urls: List<URL>,
    /** Corners of the video in clockwise order starting at top-left. */
    var coordinates: List<LngLat>,
) : MTSource {
    override var url: URL? = null

    init {
        require(coordinates.size == 4) { "Video source requires exactly 4 corner coordinates" }
        require(urls.isNotEmpty()) { "Video source requires at least one URL" }
    }

    /** Type of the source. */
    override val type: MTSourceType = MTSourceType.VIDEO

    /**
     * Updates the video corners.
     *
     * @param coordinates Four coordinates in clockwise order starting at top-left.
     * @param mapViewController Map controller that owns the style.
     */
    fun setCoordinates(
        coordinates: List<LngLat>,
        mapViewController: MTMapViewController,
    ) {
        require(coordinates.size == 4) { "Video source requires exactly 4 corner coordinates" }
        this.coordinates = coordinates
        mapViewController.style?.setCoordinatesToVideoSource(coordinates, this)
    }
}
