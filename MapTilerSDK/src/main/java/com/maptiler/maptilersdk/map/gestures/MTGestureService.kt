/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.gestures

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.options.MTDragPanOptions

class MTGestureService private constructor() {
    private val enabledGestures: MutableMap<MTGestureType, MTGesture> = mutableMapOf()

    private lateinit var bridge: MTBridge
    private lateinit var mapViewController: MTMapViewController

    companion object {
        internal fun create(
            bridge: MTBridge,
            mapViewController: MTMapViewController,
        ): MTGestureService =
            MTGestureService().apply {
                this.bridge = bridge
                this.mapViewController = mapViewController

                enabledGestures[MTGestureType.DRAG_PAN] =
                    MTGestureFactory.makeGesture(MTGestureType.DRAG_PAN, bridge)
                enabledGestures[MTGestureType.TWO_FINGERS_DRAG_PITCH] =
                    MTGestureFactory.makeGesture(MTGestureType.TWO_FINGERS_DRAG_PITCH, bridge)
                enabledGestures[MTGestureType.PINCH_ROTATE_AND_ZOOM] =
                    MTGestureFactory.makeGesture(MTGestureType.PINCH_ROTATE_AND_ZOOM, bridge)
            }
    }

    suspend fun disableGesture(type: MTGestureType) {
        enabledGestures[type]?.let { gesture ->
            gesture.disable()
            enabledGestures.remove(type)
        }
    }

    suspend fun enableDragPanGesture(options: MTDragPanOptions? = null) {
        val gesture = MTGestureFactory.makeGesture(MTGestureType.DRAG_PAN, bridge)
        enabledGestures[MTGestureType.DRAG_PAN] = gesture

        (gesture as? MTDragPanGesture)?.let {
            if (options != null) it.enable(options) else it.enable()
        }
    }

    suspend fun enablePinchRotateAndZoomGesture() {
        val gesture = MTGestureFactory.makeGesture(MTGestureType.PINCH_ROTATE_AND_ZOOM, bridge)
        enabledGestures[MTGestureType.PINCH_ROTATE_AND_ZOOM] = gesture

        (gesture as? MTPinchRotateAndZoomGesture)?.enable()
    }

    suspend fun enableTwoFingerDragPitchGesture() {
        val gesture = MTGestureFactory.makeGesture(MTGestureType.TWO_FINGERS_DRAG_PITCH, bridge)
        enabledGestures[MTGestureType.TWO_FINGERS_DRAG_PITCH] = gesture

        (gesture as? MTTwoFingersDragPitchGesture)?.enable()
    }
}
