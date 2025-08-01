/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.stylable

import com.maptiler.maptilersdk.annotations.MTMarker

/**
 * Defines methods for map styling methods.
 */
interface MTStylable {
    /**
     * Adds the marker to the map.
     *
     * @param marker Marker to add.
     */
    fun addMarker(marker: MTMarker)

    /**
     * Removes the marker from the map.
     *
     * @param marker Marker to remove.
     */
    fun removeMarker(marker: MTMarker)
}
