/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

/**
 * Statistics about an offline map pack.
 */
data class MTPackStats(
    /**
     * Expected total size in bytes.
     */
    val expectedSize: Long,
    /**
     * Expected total number of resources (tiles, glyphs, sprites, style).
     */
    val resourceCount: Int,
    /**
     * Expected number of tiles per source.
     */
    val tilesPerSource: Map<String, Int> = emptyMap(),
)
