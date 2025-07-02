/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.gestures

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.options.MTDragPanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MTGestureService(
    val scope: CoroutineScope,
) {
    private val enabledGestures: MutableMap<MTGestureType, MTGesture> = mutableMapOf()

    private lateinit var bridge: MTBridge
    private lateinit var mapViewController: MTMapViewController

    private var doubleTapSensitivity: Double = 0.4

    companion object {
        internal fun create(
            scope: CoroutineScope,
            bridge: MTBridge,
            mapViewController: MTMapViewController,
        ): MTGestureService =
            MTGestureService(scope).apply {
                this.bridge = bridge
                this.mapViewController = mapViewController

                enabledGestures[MTGestureType.DOUBLE_TAP_ZOOM_IN] =
                    MTGestureFactory.makeGesture(MTGestureType.DOUBLE_TAP_ZOOM_IN, bridge)
                enabledGestures[MTGestureType.DRAG_PAN] =
                    MTGestureFactory.makeGesture(MTGestureType.DRAG_PAN, bridge)
                enabledGestures[MTGestureType.TWO_FINGERS_DRAG_PITCH] =
                    MTGestureFactory.makeGesture(MTGestureType.TWO_FINGERS_DRAG_PITCH, bridge)
                enabledGestures[MTGestureType.PINCH_ROTATE_AND_ZOOM] =
                    MTGestureFactory.makeGesture(MTGestureType.PINCH_ROTATE_AND_ZOOM, bridge)
            }
    }

    fun disableGesture(type: MTGestureType) {
        enabledGestures[type]?.let { gesture ->

            scope.launch {
                gesture.disable()
            }

            enabledGestures.remove(type)
        }
    }

    fun enableDoubleTapZoomInGesture() {
        val gesture = MTGestureFactory.makeGesture(MTGestureType.DOUBLE_TAP_ZOOM_IN, bridge)
        enabledGestures[MTGestureType.DOUBLE_TAP_ZOOM_IN] = gesture

        scope.launch {
            (gesture as? MTPinchRotateAndZoomGesture)?.enable()
        }
    }

    fun enableDragPanGesture(options: MTDragPanOptions? = null) {
        val gesture = MTGestureFactory.makeGesture(MTGestureType.DRAG_PAN, bridge)
        enabledGestures[MTGestureType.DRAG_PAN] = gesture

        scope.launch {
            (gesture as? MTDragPanGesture)?.let {
                if (options != null) it.enable(options) else it.enable()
            }
        }
    }

    fun enablePinchRotateAndZoomGesture() {
        val gesture = MTGestureFactory.makeGesture(MTGestureType.PINCH_ROTATE_AND_ZOOM, bridge)
        enabledGestures[MTGestureType.PINCH_ROTATE_AND_ZOOM] = gesture

        scope.launch {
            (gesture as? MTPinchRotateAndZoomGesture)?.enable()
        }
    }

    fun enableTwoFingerDragPitchGesture() {
        val gesture = MTGestureFactory.makeGesture(MTGestureType.TWO_FINGERS_DRAG_PITCH, bridge)
        enabledGestures[MTGestureType.TWO_FINGERS_DRAG_PITCH] = gesture

        scope.launch {
            (gesture as? MTTwoFingersDragPitchGesture)?.enable()
        }
    }

    fun setDoubleTapSensitivity(sensitivity: Double) {
        doubleTapSensitivity = sensitivity
    }
}
