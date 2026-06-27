/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages the creation, retrieval, and deletion of offline map packs.
 */
public object MTOfflineManager {
    private val activePacks = ConcurrentHashMap<String, MTOfflinePack>()

    /**
     * Creates a new offline pack and adds it to the managed registry.
     *
     * @param context The Android context.
     * @param definition The region and style definition for the offline pack.
     * @param metadata Optional user-defined metadata to store alongside the pack.
     * @return The newly created [MTOfflinePack] instance.
     */
    public suspend fun createPack(
        context: Context,
        definition: MTOfflineRegionDefinition,
        metadata: ByteArray? = null,
    ): MTOfflinePack {
        val id = UUID.randomUUID().toString()
        val pack =
            MTOfflinePack(
                id = id,
                region = definition,
                context = context.applicationContext,
                customMetadata = metadata,
            )

        // Save metadata immediately so the pack is discoverable before downloading starts
        MTOfflineStorage.saveMetadata(context, pack.id, pack.metadata)

        activePacks[id] = pack
        return pack
    }

    /**
     * Retrieves all offline packs currently stored on disk.
     *
     * @param context The Android context.
     * @return A list of [MTOfflinePack] instances.
     */
    public suspend fun getPacks(context: Context): List<MTOfflinePack> {
        val metadataList = MTOfflineStorage.listMetadata(context)
        val packs = mutableListOf<MTOfflinePack>()

        for (metadata in metadataList) {
            val pack =
                activePacks.getOrPut(metadata.id) {
                    MTOfflinePack(metadata = metadata, context = context.applicationContext)
                }
            packs.add(pack)
        }

        return packs
    }

    /**
     * Retrieves a specific offline pack by its identifier.
     *
     * @param context The Android context.
     * @param id The unique identifier of the pack.
     * @return The [MTOfflinePack] if found, or null otherwise.
     */
    public suspend fun getPack(
        context: Context,
        id: String,
    ): MTOfflinePack? {
        // Check active registry first
        activePacks[id]?.let { return it }

        // If not in registry, try loading from disk
        val metadata = MTOfflineStorage.loadMetadata(context, id) ?: return null

        return activePacks.getOrPut(id) {
            MTOfflinePack(metadata = metadata, context = context.applicationContext)
        }
    }

    /**
     * Deletes the specified offline pack from disk and removes it from the registry.
     *
     * @param context The Android context.
     * @param pack The offline pack to remove.
     */
    public suspend fun removePack(
        context: Context,
        pack: MTOfflinePack,
    ) {
        pack.cancel() // Ensures any active downloads are stopped
        MTOfflineStorage.deletePack(context, pack.id)
        activePacks.remove(pack.id)
    }

    /**
     * Clears the active registry (useful for testing or full resets).
     * This does NOT delete the packs from disk.
     */
    internal fun clearRegistry() {
        activePacks.clear()
    }
}
