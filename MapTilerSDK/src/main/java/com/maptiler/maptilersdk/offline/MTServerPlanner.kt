/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

/**
 * A server-based planner for offline map regions that estimates download size and plans the required tiles.
 *
 * Note: This planner is currently not fully implemented as it relies on server-side manifest generation.
 */
internal class MTServerPlanner : MTOfflinePlanner {
    override suspend fun estimate(definition: MTOfflineRegionDefinition): MTTileEstimate {
        val zoomRange = MTOfflineZoomRange(definition.minZoom, definition.maxZoom)
        val tileCount = MTTileMath.estimateTileCount(definition.geometry, zoomRange, definition.padding)

        val globalLimit = MTOfflineConfiguration.effectiveGlobalLimit
        val packLimit = definition.maxTileCount ?: Int.MAX_VALUE
        val effectiveLimit = minOf(globalLimit, packLimit)

        // Internal tile limit must be enforced.
        // Once server planner is implemented keep the guard.
        if (tileCount > effectiveLimit) {
            throw MTOfflineError.ExceedsMaximumTileCount(
                limit = effectiveLimit,
                requested = tileCount,
            )
        }

        throw MTOfflineError.DownloadFailed(UnsupportedOperationException("Server planner is not yet implemented"))
    }

    override suspend fun generateManifest(definition: MTOfflineRegionDefinition): MTManifest {
        throw MTOfflineError.DownloadFailed(UnsupportedOperationException("Server planner is not yet implemented"))
    }
}
