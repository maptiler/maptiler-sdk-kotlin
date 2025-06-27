/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.gestures

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.gestures.TwoFingersDragPitchDisable
import com.maptiler.maptilersdk.commands.gestures.TwoFingersDragPitchEnable
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger

class MTTwoFingersDragPitchGesture private constructor(
    private val bridge: MTBridge,
) : MTGesture {
    override val type: MTGestureType = MTGestureType.TWO_FINGERS_DRAG_PITCH

    override suspend fun disable() {
        try {
            bridge.execute(TwoFingersDragPitchDisable())
        } catch (e: Exception) {
            MTLogger.log(e.toString(), MTLogType.ERROR)
        }
    }

    override suspend fun enable() {
        try {
            bridge.execute(TwoFingersDragPitchEnable())
        } catch (e: Exception) {
            MTLogger.log(e.toString(), MTLogType.ERROR)
        }
    }

    companion object {
        internal fun create(bridge: MTBridge): MTTwoFingersDragPitchGesture = MTTwoFingersDragPitchGesture(bridge)
    }
}
