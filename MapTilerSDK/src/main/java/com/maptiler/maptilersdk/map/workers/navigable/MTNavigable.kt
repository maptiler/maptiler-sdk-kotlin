/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.navigable

import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.types.MTPoint

interface MTNavigable {
    suspend fun panBy(offset: MTPoint)

    suspend fun panTo(coordinates: LngLat)
}
