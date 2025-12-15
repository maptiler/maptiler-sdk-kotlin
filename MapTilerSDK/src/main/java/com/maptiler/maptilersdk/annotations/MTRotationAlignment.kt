/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.annotations

/**
 * Controls how marker rotation is applied relative to map bearing.
 */
enum class MTRotationAlignment(val value: String) {
    /** Marker rotation follows the map, maintaining its bearing as the map rotates. */
    MAP("map"),

    /** Marker rotation follows the viewport, staying fixed as the map rotates. */
    VIEWPORT("viewport"),

    /** Rotation aligns to the viewport; equivalent to [VIEWPORT]. */
    AUTO("auto"),
}
