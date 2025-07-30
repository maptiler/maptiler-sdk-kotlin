/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.annotations

import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapViewController

interface MTAnnotation {
    val identifier: String
    val coordinates: LngLat

    fun setCoordinates(
        coordinates: LngLat,
        mapViewController: MTMapViewController,
    )
}
