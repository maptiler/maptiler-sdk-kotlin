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

internal class MTOfflineConfiguration {
    companion object {
        val shared = MTOfflineConfiguration()
    }

    private var _plannerType = AtomicReference(MTOfflinePlannerType.LOCAL)
    private var _userMaxTileCount = AtomicInteger(15000)

    /**
     * Hard safety limit
     */
    internal val internalMaxTileLimit: Int = 15000

    /**
     * The default expiration interval for offline packs (30 days).
     */
    internal val defaultExpirationInterval: Long = 30L * 24 * 60 * 60 * 1000

    /**
     * The default grace period before an expired pack is deleted (7 days).
     */
    internal val defaultGracePeriod: Long = 7L * 24 * 60 * 60 * 1000

    internal var plannerType: MTOfflinePlannerType
        get() = _plannerType.get()
        set(value) = _plannerType.set(value)

    /**
     * The global limit set by the SDK consumer.
     */
    internal var userMaxTileCount: Int
        get() = _userMaxTileCount.get()
        set(value) = _userMaxTileCount.set(value)

    /**
     * The effective global limit (most restrictive of internal vs user).
     */
    internal val effectiveGlobalLimit: Int
        get() = minOf(userMaxTileCount, internalMaxTileLimit)
}
