/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.net.Uri

/**
 * A utility for generating glyph ranges and formatting glyph URLs for offline use.
 */
internal object MTGlyphHelper {
    /**
     * The default maximum Unicode index to cover (Basic Multilingual Plane).
     */
    const val DEFAULT_MAX_INDEX = 65535

    /**
     * Generates the standard 256-step Unicode character ranges.
     *
     * @param maxIndex The maximum Unicode index to cover.
     * @return A list of [MTGlyphRange] objects.
     */
    fun generateRanges(maxIndex: Int = DEFAULT_MAX_INDEX): List<MTGlyphRange> {
        val ranges = mutableListOf<MTGlyphRange>()
        // Standard Mapbox/MapLibre glyphs are requested in chunks of 256.
        // Start index is a multiple of 256, end index is start + 255.
        for (start in 0..maxIndex step 256) {
            val end = start + 255
            ranges.add(MTGlyphRange(start, end))
        }
        return ranges
    }

    /**
     * Formats a glyph URL template with a font stack and a range.
     *
     * @param template The glyph URL template.
     * @param fonts The list of fonts in the font stack.
     * @param range The glyph range.
     * @return The formatted URL string.
     */
    fun format(
        template: String,
        fonts: List<String>,
        range: MTGlyphRange,
    ): String {
        val fontStack = fonts.joinToString(",")
        return format(template, fontStack, range.toString())
    }

    /**
     * Formats a glyph URL template with a font stack string and a range string.
     *
     * @param template The glyph URL template.
     * @param fontStack The font stack string.
     * @param range The range string.
     * @return The formatted URL string.
     */
    fun format(
        template: String,
        fontStack: String,
        range: String,
    ): String {
        val encodedFontStack = Uri.encode(fontStack)

        return template
            .replace("{fontstack}", encodedFontStack)
            .replace("{range}", range)
    }
}
