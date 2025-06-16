/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style

/**
 * Variants of the reference styles.
 */
enum class MTMapStyleVariant(
    val value: String,
) {
    /**
     * Default variant.
     */
    DEFAULT_VARIANT("default"),

    /**
     * Dark variant.
     */
    DARK("dark"),

    /**
     * Light variant.
     */
    LIGHT("light"),

    /**
     * Pastel variant.
     */
    PASTEL("pastel"),

    /**
     * Night variant.
     */
    NIGHT("night"),

    /**
     * Shiny variant.
     */
    SHINY("shiny"),

    /**
     * Topographique variant.
     */
    TOPOGRAPHIQUE("topographique"),

    /**
     * Lite variant.
     */
    LITE("lite"),

    /**
     * Lines variant.
     */
    LINES("lines"),

    /**
     * Background variant
     */
    BACKGROUND("background"),

    /**
     * Vivid variant
     */
    VIVID("vivid"),
}
