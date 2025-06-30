/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.navigable

import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.types.MTPoint

/**
 * Defines methods for navigating the map.
 */
interface MTNavigable {
    /**
     * Pans the map by the specified offset.
     *
     * @param offset Offset to pan by.
     */
    fun panBy(offset: MTPoint)

    /**
     * Pans the map to the specified location with an animated transition.
     *
     * @param coordinates Coordinates to pan to.
     */
    fun panTo(coordinates: LngLat)
}
