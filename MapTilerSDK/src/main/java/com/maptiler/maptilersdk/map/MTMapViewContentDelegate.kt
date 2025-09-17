/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map

import com.maptiler.maptilersdk.events.MTEvent
import com.maptiler.maptilersdk.map.types.MTData

/**
 * Secondary event listener for map events, intended for overlay/content components
 * that must react to map movement (e.g., custom Compose annotation views).
 *
 * This delegate is additive and does not replace [MTMapViewDelegate].
 */
interface MTMapViewContentDelegate {
    /**
     * Called whenever a map event is fired.
     */
    fun onEvent(
        event: MTEvent,
        data: MTData?,
    )
}
