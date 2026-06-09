/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import kotlinx.serialization.Serializable

/**
 * Represents the current state of an offline pack download.
 */
@Serializable
enum class MTOfflinePackState {
    /**
     * The pack has been created but download has not started.
     */
    PENDING,

    /**
     * The pack is currently downloading.
     */
    DOWNLOADING,

    /**
     * The pack download was paused.
     */
    PAUSED,

    /**
     * The pack download was canceled.
     */
    CANCELED,

    /**
     * The pack download completed successfully.
     */
    COMPLETED,

    /**
     * The pack download failed.
     */
    FAILED,

    /**
     * The pack has expired and its tiles are no longer usable.
     */
    EXPIRED,
}

/**
 * Metadata information about an offline pack.
 *
 * This model is used to persist pack information such as its identifier,
 * current state, total size, and creation date.
 */
@Serializable
data class MTOfflinePackMetadata(
    /**
     * The unique identifier of the pack.
     */
    val id: String,
    /**
     * The current state of the pack.
     */
    var state: MTOfflinePackState,
    /**
     * The total size of the pack in bytes.
     */
    var size: Long,
    /**
     * The date when the pack was created (timestamp in milliseconds).
     */
    val createdAt: Long,
    /**
     * The date when the pack expires (timestamp in milliseconds).
     */
    var expiresAt: Long,
    /**
     * Optional custom data, typically used to store application-specific context (e.g. JSON data).
     */
    val context: ByteArray? = null,
    /**
     * The region definition specifying the bounding box, zoom levels, and style.
     */
    val region: MTOfflineRegionDefinition,
    /**
     * Total number of resources required for the pack.
     */
    var totalResources: Int,
    /**
     * Total number of tile resources required for the pack.
     */
    var totalTileResources: Int,
    /**
     * Number of resources that have been successfully downloaded.
     */
    var downloadedResources: Int,
) {
    /**
     * Returns true if the pack has passed its expiration date.
     */
    val isExpired: Boolean
        get() = System.currentTimeMillis() > expiresAt

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MTOfflinePackMetadata

        if (id != other.id) return false
        if (state != other.state) return false
        if (size != other.size) return false
        if (createdAt != other.createdAt) return false
        if (expiresAt != other.expiresAt) return false
        if (context != null) {
            if (other.context == null) return false
            if (!context.contentEquals(other.context)) return false
        } else if (other.context != null) {
            return false
        }
        if (region != other.region) return false
        if (totalResources != other.totalResources) return false
        if (totalTileResources != other.totalTileResources) return false
        if (downloadedResources != other.downloadedResources) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + expiresAt.hashCode()
        result = 31 * result + (context?.contentHashCode() ?: 0)
        result = 31 * result + region.hashCode()
        result = 31 * result + totalResources
        result = 31 * result + totalTileResources
        result = 31 * result + downloadedResources
        return result
    }
}
