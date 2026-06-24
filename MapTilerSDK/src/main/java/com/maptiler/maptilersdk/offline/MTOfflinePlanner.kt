/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

/**
 * Interface defining the core interface for planning an offline download.
 */
internal interface MTOfflinePlanner {
    /**
     * Estimates the size and resources required for an offline region.
     *
     * @param definition The region definition to estimate.
     * @return An [MTTileEstimate] containing the results.
     */
    suspend fun estimate(definition: MTOfflineRegionDefinition): MTTileEstimate

    /**
     * Generates a manifest detailing all resources required for an offline region.
     *
     * @param definition The region definition to generate a manifest for.
     * @return An [MTManifest] containing the required resources.
     */
    suspend fun generateManifest(definition: MTOfflineRegionDefinition): MTManifest
}
