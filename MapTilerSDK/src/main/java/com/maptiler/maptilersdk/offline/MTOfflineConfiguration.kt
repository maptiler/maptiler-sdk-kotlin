/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

internal enum class MTOfflinePlannerType {
    LOCAL,
    SERVER,
}

/**
 * Global configuration for offline mapping.
 */
internal object MTOfflineConfiguration {
    private var _plannerType = AtomicReference(MTOfflinePlannerType.LOCAL)
    private var _userMaxTileCount = AtomicInteger(15000)

    /**
     * Hard safety limit for the number of tiles in a single pack.
     */
    internal const val INTERNAL_MAX_TILE_LIMIT: Int = 15000

    /**
     * The default expiration interval for offline packs (30 days) in milliseconds.
     */
    internal const val DEFAULT_EXPIRATION_INTERVAL: Long = 30L * 24 * 60 * 60 * 1000

    /**
     * The default grace period before an expired pack is deleted (7 days) in milliseconds.
     */
    internal const val DEFAULT_GRACE_PERIOD: Long = 7L * 24 * 60 * 60 * 1000

    /**
     * The type of planner to use for generating offline manifests.
     */
    internal var plannerType: MTOfflinePlannerType
        get() = _plannerType.get()
        set(value) = _plannerType.set(value)

    /**
     * The global limit for tile count set by the SDK consumer.
     */
    internal var userMaxTileCount: Int
        get() = _userMaxTileCount.get()
        set(value) = _userMaxTileCount.set(value)

    /**
     * The effective global limit (most restrictive of internal vs user).
     */
    internal val effectiveGlobalLimit: Int
        get() = minOf(userMaxTileCount, INTERNAL_MAX_TILE_LIMIT)
}
