/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.gestures

/**
 * Gesture types available in the SDK.
 */
enum class MTGestureType {
    /**
     * Double tap to zoom in.
     */
    DOUBLE_TAP_ZOOM_IN,

    /**
     * Drag and pan.
     */
    DRAG_PAN,

    /**
     * Pitch shifting with two finger drag.
     */
    TWO_FINGERS_DRAG_PITCH,

    /**
     * Pinching to rotate and zoom.
     */
    PINCH_ROTATE_AND_ZOOM,
}
