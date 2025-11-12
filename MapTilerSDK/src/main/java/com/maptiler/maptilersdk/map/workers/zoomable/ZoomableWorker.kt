/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.zoomable

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType.DoubleValue
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType.StringValue
import com.maptiler.maptilersdk.commands.navigation.GetMaxZoom
import com.maptiler.maptilersdk.commands.navigation.GetMinZoom
import com.maptiler.maptilersdk.commands.navigation.GetZoom
import com.maptiler.maptilersdk.commands.navigation.SetMaxZoom
import com.maptiler.maptilersdk.commands.navigation.SetMinZoom
import com.maptiler.maptilersdk.commands.navigation.SetZoom
import com.maptiler.maptilersdk.commands.navigation.ZoomIn
import com.maptiler.maptilersdk.commands.navigation.ZoomOut
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class ZoomableWorker(
    private val bridge: MTBridge,
    private val scope: CoroutineScope,
) : MTZoomable {
    override fun zoomIn() {
        scope.launch {
            bridge.execute(
                ZoomIn(),
            )
        }
    }

    override fun zoomOut() {
        scope.launch {
            bridge.execute(
                ZoomOut(),
            )
        }
    }

    override suspend fun getZoom(): Double {
        val returnTypeValue =
            bridge.execute(
                GetZoom(),
            )

        return when (returnTypeValue) {
            is StringValue -> returnTypeValue.value.toDouble()
            is DoubleValue -> returnTypeValue.value
            else -> 0.0
        }
    }

    override suspend fun getMaxZoom(): Double {
        val returnTypeValue =
            bridge.execute(
                GetMaxZoom(),
            )

        return when (returnTypeValue) {
            is StringValue -> returnTypeValue.value.toDouble()
            is DoubleValue -> returnTypeValue.value
            else -> 0.0
        }
    }

    override suspend fun getMinZoom(): Double {
        val returnTypeValue =
            bridge.execute(
                GetMinZoom(),
            )

        return when (returnTypeValue) {
            is StringValue -> returnTypeValue.value.toDouble()
            is DoubleValue -> returnTypeValue.value
            else -> 0.0
        }
    }

    override fun setZoom(zoom: Double) {
        scope.launch {
            bridge.execute(
                SetZoom(zoom),
            )
        }
    }

    override fun setMaxZoom(maxZoom: Double) {
        scope.launch {
            bridge.execute(
                SetMaxZoom(maxZoom),
            )
        }
    }

    override fun setMinZoom(minZoom: Double) {
        scope.launch {
            bridge.execute(
                SetMinZoom(minZoom),
            )
        }
    }
}
