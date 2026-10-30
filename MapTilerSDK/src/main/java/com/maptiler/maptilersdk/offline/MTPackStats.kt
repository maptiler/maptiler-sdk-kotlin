/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

/**
 * Tile statistics for a single source of an offline pack.
 */
data class MTSourceStats(
    /**
     * The source identifier from the style.
     */
    val id: String,
    /**
     * The source type (`vector`, `raster` or `raster-dem`).
     */
    val type: String?,
    /**
     * The lowest zoom level downloaded for this source.
     */
    val minZoom: Int,
    /**
     * The highest zoom level downloaded for this source. It may be lower than the requested maximum zoom
     * when the source has no data beyond it; the map overzooms the downloaded tiles instead.
     */
    val maxZoom: Int,
    /**
     * The number of tiles downloaded per zoom level.
     */
    val tilesPerZoom: Map<Int, Long>,
) {
    /**
     * The number of tiles downloaded for this source.
     */
    val tileCount: Long
        get() = tilesPerZoom.values.sum()
}

/**
 * Statistics about an offline map pack.
 */
data class MTPackStats(
    /**
     * Expected total size in bytes.
     *
     * This is a rough estimate based on average resource sizes. Real tile sizes vary widely between
     * dense cities and empty areas, so the actual size can differ significantly.
     */
    val expectedSize: Long,
    /**
     * Expected total number of resources (tiles, glyphs, sprites, style). Every resource is one request.
     * Saturates at [Int.MAX_VALUE]; see [totalResourceCount] for the exact value.
     */
    val resourceCount: Int,
    /**
     * Expected number of tiles per source. Saturates at [Int.MAX_VALUE].
     */
    val tilesPerSource: Map<String, Int> = emptyMap(),
    /**
     * Per-source tile statistics, including the downloaded zoom range and tiles per zoom level.
     */
    val sources: List<MTSourceStats> = emptyList(),
    /**
     * The number of glyph resources.
     */
    val glyphCount: Int = 0,
    /**
     * The number of sprite resources.
     */
    val spriteCount: Int = 0,
    /**
     * The number of style resources.
     */
    val styleCount: Int = 0,
    /**
     * The maximum number of resources a single pack may contain for this region.
     */
    val resourceLimit: Int = MTOfflineConfiguration.effectiveGlobalLimit,
) {
    /**
     * The total number of tiles.
     */
    val tileCount: Long
        get() = if (sources.isEmpty()) tilesPerSource.values.sumOf { it.toLong() } else sources.sumOf { it.tileCount }

    /**
     * The exact total number of resources.
     */
    val totalResourceCount: Long
        get() = if (sources.isEmpty()) resourceCount.toLong() else tileCount + glyphCount + spriteCount + styleCount

    /**
     * Whether the region exceeds the resource limit and cannot be downloaded as a single pack.
     */
    val exceedsLimit: Boolean
        get() = totalResourceCount > resourceLimit

    /**
     * The minimum number of packs needed to download the region.
     *
     * Every pack downloads the style, sprites and glyphs again, so only the remaining part of the limit is
     * available for tiles. Splitting a region adds some tiles along the split edges, so the real number of
     * packs can be higher. `null` when the style resources alone exceed the limit.
     */
    val minimumPackCount: Long?
        get() {
            val tileBudget = resourceLimit - (glyphCount + spriteCount + styleCount)
            if (tileBudget <= 0) return null
            return maxOf(1L, (tileCount + tileBudget - 1) / tileBudget)
        }
}
