/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.navigable

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.navigation.EaseTo
import com.maptiler.maptilersdk.commands.navigation.FlyTo
import com.maptiler.maptilersdk.commands.navigation.JumpTo
import com.maptiler.maptilersdk.commands.navigation.PanBy
import com.maptiler.maptilersdk.commands.navigation.PanTo
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import com.maptiler.maptilersdk.map.options.MTFlyToOptions
import com.maptiler.maptilersdk.map.types.MTPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class NavigableWorker(
    private val bridge: MTBridge,
    private val scope: CoroutineScope,
) : MTNavigable {
    override fun panBy(offset: MTPoint) {
        scope.launch {
            bridge.execute(
                PanBy(offset),
            )
        }
    }

    override fun panTo(coordinates: LngLat) {
        scope.launch {
            bridge.execute(
                PanTo(coordinates),
            )
        }
    }

    override fun flyTo(
        cameraOptions: MTCameraOptions,
        flyToOptions: MTFlyToOptions?,
    ) {
        scope.launch {
            bridge.execute(
                FlyTo(cameraOptions, flyToOptions),
            )
        }
    }

    override fun jumpTo(cameraOptions: MTCameraOptions) {
        scope.launch {
            bridge.execute(
                JumpTo(cameraOptions),
            )
        }
    }

    override fun easeTo(cameraOptions: MTCameraOptions) {
        scope.launch {
            bridge.execute(
                EaseTo(cameraOptions),
            )
        }
    }
}
