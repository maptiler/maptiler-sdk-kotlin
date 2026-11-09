/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map

import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.offline.MTOfflineRegionDefinition

/**
 * Tracks offline pack loading relative to map initialization.
 *
 * An offline pack requested before the map is initialized is kept as pending and applied
 * by the controller during initialization, so the map starts directly with the offline style.
 */
internal class OfflinePackLoadState {
    /**
     * Offline style and optional region limits to apply on map initialization.
     */
    internal data class PendingOfflinePack(
        val referenceStyle: MTMapReferenceStyle,
        val region: MTOfflineRegionDefinition?,
    )

    private var isMapInitialized = false
    private var pendingOfflinePack: PendingOfflinePack? = null

    /**
     * Whether an offline pack is loaded or pending to be loaded.
     */
    @Volatile
    var isPackLoaded: Boolean = false
        private set

    /**
     * Marks the pack as loaded and keeps it pending if the map is not initialized yet.
     *
     * @return True if the pack was queued for map initialization, false if the map is already initialized.
     */
    @Synchronized
    fun loadPack(pack: PendingOfflinePack): Boolean {
        isPackLoaded = true

        if (isMapInitialized) {
            return false
        }

        pendingOfflinePack = pack
        return true
    }

    /**
     * Marks the map as initialized and returns the pending pack, if any.
     */
    @Synchronized
    fun onMapInitialized(): PendingOfflinePack? {
        isMapInitialized = true

        return pendingOfflinePack.also { pendingOfflinePack = null }
    }

    /**
     * Clears the loaded and pending pack.
     */
    @Synchronized
    fun unloadPack() {
        isPackLoaded = false
        pendingOfflinePack = null
    }
}
