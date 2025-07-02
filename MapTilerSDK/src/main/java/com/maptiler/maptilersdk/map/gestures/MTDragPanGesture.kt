/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.gestures

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.gestures.DragPanDisable
import com.maptiler.maptilersdk.commands.gestures.DragPanEnable
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger
import com.maptiler.maptilersdk.map.options.MTDragPanOptions

class MTDragPanGesture private constructor(
    private val bridge: MTBridge,
) : MTGesture {
    override val type: MTGestureType = MTGestureType.DRAG_PAN

    /**
     * Disables the gesture on the map.
     */
    override suspend fun disable() {
        try {
            bridge.execute(DragPanDisable())
        } catch (e: Exception) {
            MTLogger.log(e.toString(), MTLogType.ERROR)
        }
    }

    /**
     * Enables the gesture on the map.
     */
    override suspend fun enable() {
        try {
            bridge.execute(DragPanEnable(options = null))
        } catch (e: Exception) {
            MTLogger.log(e.toString(), MTLogType.ERROR)
        }
    }

    /**
     * Enables the gesture on the map using specified options.
     *
     * @param options Drag options to use.
     */
    suspend fun enable(options: MTDragPanOptions) {
        try {
            bridge.execute(DragPanEnable(options = options))
        } catch (e: Exception) {
            MTLogger.log(e.toString(), MTLogType.ERROR)
        }
    }

    companion object {
        internal fun create(bridge: MTBridge): MTDragPanGesture = MTDragPanGesture(bridge)
    }
}
