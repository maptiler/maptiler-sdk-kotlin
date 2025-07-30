/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.annotations

import android.graphics.Bitmap
import android.graphics.Color
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapViewController
import java.util.UUID

/**
 * Annotation element that can be added to the map.
 */
class MTMarker(
    override val identifier: String = "mark${UUID.randomUUID().toString().replace("-", "")}",
    private var _coordinates: LngLat,
) : MTAnnotation {
    /**
     * Position of the marker on the map.
     */
    override val coordinates: LngLat
        get() = _coordinates

    /**
     * Color of the marker.
     */
    var color: Int? = Color.BLUE

    /**
     * Boolean indicating whether marker is draggable.
     */
    var draggable: Boolean? = false

    /**
     * Custom icon to use for marker.
     */
    var icon: Bitmap? = null

    private var tapThreshold: Double = 30.0

    constructor(
        coordinates: LngLat,
        color: Int? = Color.BLUE,
        icon: Bitmap? = null,
        draggable: Boolean? = false,
    ) : this(identifier = "mark${UUID.randomUUID().toString().replace("-", "")}", _coordinates = coordinates) {
        this.color = color
        this.icon = icon
        this.draggable = draggable
    }

    /**
     * Sets coordinates for the marker.
     *
     * @param coordinates Position of the marker.
     */
    override fun setCoordinates(
        coordinates: LngLat,
        mapViewController: MTMapViewController,
    ) {
        this._coordinates = coordinates

        mapViewController.setCoordinatesToMarker(this)
    }
}
