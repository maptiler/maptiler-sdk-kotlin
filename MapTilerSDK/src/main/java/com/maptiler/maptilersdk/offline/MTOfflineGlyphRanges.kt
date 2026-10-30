/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The Unicode ranges of label glyphs an offline pack downloads for each font of its style.
 *
 * Glyphs come in ranges of 256 code points. Downloading every range of every font is large (about 38 MB per
 * font, mostly Chinese, Japanese and Korean characters), so packs download only the ranges they need. Characters
 * outside the downloaded ranges are not rendered offline.
 */
@Serializable
sealed class MTOfflineGlyphRanges {
    /**
     * Latin (including extended Latin and Vietnamese), Greek and Cyrillic, plus common punctuation and symbols.
     */
    @Serializable
    @SerialName("latinGreekCyrillic")
    data object LatinGreekCyrillic : MTOfflineGlyphRanges()

    /**
     * Every range of the Basic Multilingual Plane (256 ranges per font).
     */
    @Serializable
    @SerialName("all")
    data object All : MTOfflineGlyphRanges()

    /**
     * Custom ranges, given by the first code point of each range (a multiple of 256, e.g. `0x4E00` for CJK).
     */
    @Serializable
    @SerialName("custom")
    data class Custom(
        val starts: List<Int>,
    ) : MTOfflineGlyphRanges()

    /**
     * The first code points of the downloaded ranges, sorted and without duplicates.
     */
    val rangeStarts: List<Int>
        get() =
            when (this) {
                LatinGreekCyrillic -> LATIN_GREEK_CYRILLIC_STARTS
                All -> (0..MTGlyphHelper.DEFAULT_MAX_INDEX step 256).toList()
                is Custom ->
                    starts
                        .map { (it / 256) * 256 }
                        .filter { it in 0..MTGlyphHelper.DEFAULT_MAX_INDEX }
                        .distinct()
                        .sorted()
            }

    internal val ranges: List<MTGlyphRange>
        get() = rangeStarts.map { MTGlyphRange(it, it + 255) }

    companion object {
        /**
         * The first code points of the ranges in [LatinGreekCyrillic].
         */
        val LATIN_GREEK_CYRILLIC_STARTS =
            listOf(
                // Basic Latin, Latin-1 Supplement
                0x0000,
                // Latin Extended-A and -B
                0x0100,
                // Latin Extended-B, IPA, spacing modifiers
                0x0200,
                // Combining diacritics, Greek
                0x0300,
                // Cyrillic
                0x0400,
                // Cyrillic Supplement
                0x0500,
                // Phonetic extensions
                0x1D00,
                // Latin Extended Additional (Vietnamese)
                0x1E00,
                // Greek Extended
                0x1F00,
                // General punctuation, super- and subscripts, currency symbols
                0x2000,
                // Letterlike symbols, number forms, arrows
                0x2100,
            )
    }
}
