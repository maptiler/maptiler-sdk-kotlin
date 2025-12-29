/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

/**
 * Halo (atmospheric glow) configuration for the globe.
 *
 * When enabled, renders a radial gradient-based glow around the globe. Requires globe projection
 * to be visible. Prefer setting via map options at initialization or dynamically via MTStyle.setHalo.
 */
class MTHalo(
    /** Controls the halo size. Typical range is 0.0..2.0. */
    val scale: Double? = null,
    /**
     * Defines the radial gradient as a list of stops, each as a pair of normalized position (0..1)
     * and a color string (e.g., "transparent", "#RRGGBB", "rgba(r,g,b,a)").
     */
    val stops: List<MTHaloStop>? = null,
)

/**
 * A single halo gradient stop at a normalized distance with a color string.
 */
data class MTHaloStop(
    val position: Double,
    val color: String,
)
