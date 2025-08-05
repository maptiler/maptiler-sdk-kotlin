/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.annotations

import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapViewController
import java.util.UUID

class MTTextPopup(
    override val identifier: String = "mark${UUID.randomUUID().toString().replace("-", "")}",
    private var _coordinates: LngLat,
) : MTAnnotation {
    /**
     * Position of the popup on the map.
     */
    override val coordinates: LngLat
        get() = _coordinates

    /**
     * Text content of the popup.
     */
    var text: String = ""

    /**
     * The pixel distance from the popup's coordinates.
     */
    var offset: Double? = 0.0

    constructor(
        coordinates: LngLat,
        text: String,
    ) : this(identifier = "mark${UUID.randomUUID().toString().replace("-", "")}", _coordinates = coordinates) {
        this.text = text
    }

    constructor(
        coordinates: LngLat,
        text: String,
        offset: Double,
    ) : this(identifier = "mark${UUID.randomUUID().toString().replace("-", "")}", _coordinates = coordinates) {
        this.text = text
        this.offset = offset
    }

    /**
     * Sets coordinates for the popup.
     *
     * @param coordinates Position of the popup.
     */
    override fun setCoordinates(
        coordinates: LngLat,
        mapViewController: MTMapViewController,
    ) {
        this._coordinates = coordinates

        mapViewController.setCoordinatesToTextPopup(this)
    }
}
