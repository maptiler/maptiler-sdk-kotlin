/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.annotations

/**
 * Controls how marker pitch is aligned when the map is tilted.
 */
enum class MTPitchAlignment(val value: String) {
    /** Marker is aligned to the plane of the map. */
    MAP("map"),

    /** Marker is aligned to the plane of the viewport. */
    VIEWPORT("viewport"),

    /** Marker pitch alignment automatically matches the rotation alignment. */
    AUTO("auto"),
}
