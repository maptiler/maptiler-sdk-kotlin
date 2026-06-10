/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import kotlinx.serialization.Serializable

/**
 * Represents the download progress of an offline pack.
 */
@Serializable
data class MTOfflinePackProgress(
    /**
     * Total number of resources required for the pack.
     */
    val totalResources: Int,
    /**
     * Number of resources that have been successfully downloaded.
     */
    val downloadedResources: Int,
    /**
     * Total number of tile resources required for the pack.
     */
    val totalTileResources: Int,
) {
    /**
     * The completion percentage (0.0 to 1.0).
     */
    val percentage: Double
        get() =
            if (totalResources > 0) {
                downloadedResources.toDouble() / totalResources.toDouble()
            } else {
                0.0
            }
}
