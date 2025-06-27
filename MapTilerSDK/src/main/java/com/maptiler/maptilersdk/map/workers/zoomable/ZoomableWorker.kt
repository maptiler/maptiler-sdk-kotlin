/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.zoomable

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType.DoubleValue
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType.StringValue
import com.maptiler.maptilersdk.commands.navigation.GetZoom
import com.maptiler.maptilersdk.commands.navigation.SetMaxZoom
import com.maptiler.maptilersdk.commands.navigation.SetMinZoom
import com.maptiler.maptilersdk.commands.navigation.SetZoom
import com.maptiler.maptilersdk.commands.navigation.ZoomIn
import com.maptiler.maptilersdk.commands.navigation.ZoomOut

internal class ZoomableWorker(
    private val bridge: MTBridge,
) : MTZoomable {
    override suspend fun zoomIn() {
        bridge.execute(
            ZoomIn(),
        )
    }

    override suspend fun zoomOut() {
        bridge.execute(
            ZoomOut(),
        )
    }

    override suspend fun getZoom(): Double {
        val returnTypeValue =
            bridge.execute(
                GetZoom(),
            )

        when (returnTypeValue) {
            is StringValue -> return returnTypeValue.value.toDouble()
            is DoubleValue -> return returnTypeValue.value
            else -> return 0.0
        }
    }

    override suspend fun setZoom(zoom: Double) {
        bridge.execute(
            SetZoom(zoom),
        )
    }

    override suspend fun setMaxZoom(maxZoom: Double) {
        bridge.execute(
            SetMaxZoom(maxZoom),
        )
    }

    override suspend fun setMinZoom(minZoom: Double) {
        bridge.execute(
            SetMinZoom(minZoom),
        )
    }
}
