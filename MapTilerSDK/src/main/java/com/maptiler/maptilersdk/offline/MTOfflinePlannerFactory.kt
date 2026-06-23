/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

/**
 * Factory for creating [MTOfflinePlanner] instances.
 */
internal object MTOfflinePlannerFactory {
    /**
     * Creates an [MTOfflinePlanner] based on the current configuration.
     *
     * @return A new [MTOfflinePlanner] instance.
     */
    fun createPlanner(): MTOfflinePlanner {
        return when (MTOfflineConfiguration.plannerType) {
            MTOfflinePlannerType.LOCAL -> MTLocalPlanner()
            MTOfflinePlannerType.SERVER -> {
                // Fallback to local if server planner is not yet implemented or available
                MTLocalPlanner()
            }
        }
    }
}
