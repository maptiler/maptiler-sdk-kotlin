/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.gestures

/**
 * Represents a gesture on the map.
 */
interface MTGesture {
    val type: MTGestureType

    suspend fun disable()

    suspend fun enable()
}
