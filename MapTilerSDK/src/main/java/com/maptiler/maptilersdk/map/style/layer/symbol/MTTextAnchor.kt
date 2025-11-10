/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.symbol

/**
 * Positions the text relative to the symbol anchor.
 */
enum class MTTextAnchor(val value: String) {
    CENTER("center"),
    LEFT("left"),
    RIGHT("right"),
    TOP("top"),
    BOTTOM("bottom"),
    TOP_LEFT("top-left"),
    TOP_RIGHT("top-right"),
    BOTTOM_LEFT("bottom-left"),
    BOTTOM_RIGHT("bottom-right"),
    ;

    companion object {
        fun from(raw: String): MTTextAnchor? = entries.firstOrNull { it.value == raw }
    }
}
