/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

/**
 * Represents the estimated size and resource count for an offline region.
 */
internal data class MTTileEstimate(
    /**
     * The detailed pack statistics.
     */
    val stats: MTPackStats,
)
