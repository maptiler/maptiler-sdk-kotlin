/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.gestures

import com.maptiler.maptilersdk.bridge.MTBridge

internal object MTGestureFactory {
    fun makeGesture(
        type: MTGestureType,
        bridge: MTBridge,
    ): MTGesture =
        when (type) {
            MTGestureType.DOUBLE_TAP_ZOOM_IN -> MTDoubleTapZoomInGesture.create(bridge)
            MTGestureType.DRAG_PAN -> MTDragPanGesture.create(bridge)
            MTGestureType.TWO_FINGERS_DRAG_PITCH -> MTTwoFingersDragPitchGesture.create(bridge)
            MTGestureType.PINCH_ROTATE_AND_ZOOM -> MTPinchRotateAndZoomGesture.create(bridge)
        }
}
