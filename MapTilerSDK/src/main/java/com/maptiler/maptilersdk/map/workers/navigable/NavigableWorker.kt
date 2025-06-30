/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.navigable

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.navigation.PanBy
import com.maptiler.maptilersdk.commands.navigation.PanTo
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.types.MTPoint

internal class NavigableWorker(
    private val bridge: MTBridge,
) : MTNavigable {
    override suspend fun panBy(offset: MTPoint) {
        bridge.execute(
            PanBy(offset),
        )
    }

    override suspend fun panTo(coordinates: LngLat) {
        bridge.execute(
            PanTo(coordinates),
        )
    }
}
