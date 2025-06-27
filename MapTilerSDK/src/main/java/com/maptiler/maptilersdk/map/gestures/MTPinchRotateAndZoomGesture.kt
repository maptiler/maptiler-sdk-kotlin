/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.gestures

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.gestures.PinchRotateAndZoomDisable
import com.maptiler.maptilersdk.commands.gestures.PinchRotateAndZoomEnable
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger

class MTPinchRotateAndZoomGesture private constructor(
    private val bridge: MTBridge,
) : MTGesture {
    override val type: MTGestureType = MTGestureType.PINCH_ROTATE_AND_ZOOM

    override suspend fun disable() {
        try {
            bridge.execute(PinchRotateAndZoomDisable())
        } catch (e: Exception) {
            MTLogger.log(e.toString(), MTLogType.ERROR)
        }
    }

    override suspend fun enable() {
        try {
            bridge.execute(PinchRotateAndZoomEnable())
        } catch (e: Exception) {
            MTLogger.log(e.toString(), MTLogType.ERROR)
        }
    }

    companion object {
        internal fun create(bridge: MTBridge): MTPinchRotateAndZoomGesture = MTPinchRotateAndZoomGesture(bridge)
    }
}
