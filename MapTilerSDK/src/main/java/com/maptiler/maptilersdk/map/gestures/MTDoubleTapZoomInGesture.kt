/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.gestures

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.gestures.DoubleTapZoomDisable
import com.maptiler.maptilersdk.commands.gestures.DoubleTapZoomEnable
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger

class MTDoubleTapZoomInGesture private constructor(
    private val bridge: MTBridge,
) : MTGesture {
    override val type: MTGestureType = MTGestureType.DOUBLE_TAP_ZOOM_IN

    /**
     * Disables the gesture on the map.
     */
    override suspend fun disable() {
        try {
            bridge.execute(DoubleTapZoomDisable())
        } catch (e: Exception) {
            MTLogger.log(e.toString(), MTLogType.ERROR)
        }
    }

    /**
     * Enables the gesture on the map.
     */
    override suspend fun enable() {
        try {
            bridge.execute(DoubleTapZoomEnable())
        } catch (e: Exception) {
            MTLogger.log(e.toString(), MTLogType.ERROR)
        }
    }

    companion object {
        internal fun create(bridge: MTBridge): MTDoubleTapZoomInGesture = MTDoubleTapZoomInGesture(bridge)
    }
}
