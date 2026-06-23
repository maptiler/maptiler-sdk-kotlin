/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

/**
 * Represents a 256-step Unicode character range used for downloading map glyphs.
 */
internal data class MTGlyphRange(
    /**
     * The start index of the range (inclusive).
     */
    val start: Int,
    /**
     * The end index of the range (inclusive).
     */
    val end: Int,
) {
    /**
     * A string representation of the range in the format "start-end" (e.g., "0-255").
     */
    override fun toString(): String = "$start-$end"
}
