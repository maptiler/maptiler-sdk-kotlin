/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.stylable

import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.annotations.MTTextPopup

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

    /**
     * Adds a text popup to the map.
     *
     * @param popup Popup to add.
     */
    fun addTextPopup(popup: MTTextPopup)

    /**
     * Removes a text popup from the map.
     *
     * @param popup Popup to remove.
     */
    fun removeTextPopup(popup: MTTextPopup)
}
