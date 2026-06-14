/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.helpers.InstantSerializer
import com.maptiler.maptilersdk.helpers.JsonConfig
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

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
@Serializable(with = MTOfflinePackMetadataSerializer::class)
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
     * The date when the pack was created.
     */
    val createdAt: Instant,
    /**
     * The date when the pack expires.
     */
    var expiresAt: Instant,
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
        get() = Instant.now().isAfter(expiresAt)

    /**
     * Encodes this metadata to a JSON string.
     */
    fun toJson(): String = JsonConfig.json.encodeToString(serializer(), this)

    companion object {
        /**
         * Decodes metadata from a JSON string.
         */
        fun fromJson(json: String): MTOfflinePackMetadata = JsonConfig.json.decodeFromString(serializer(), json)
    }

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

@Serializable
private data class MTOfflinePackMetadataSurrogate(
    val id: String,
    var state: MTOfflinePackState,
    var size: Long,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant,
    @Serializable(with = InstantSerializer::class)
    val expiresAt: Instant? = null,
    val context: ByteArray? = null,
    val region: MTOfflineRegionDefinition,
    var totalResources: Int = 0,
    var totalTileResources: Int = 0,
    var downloadedResources: Int = 0,
)

internal object MTOfflinePackMetadataSerializer : KSerializer<MTOfflinePackMetadata> {
    override val descriptor: SerialDescriptor = MTOfflinePackMetadataSurrogate.serializer().descriptor

    override fun serialize(
        encoder: Encoder,
        value: MTOfflinePackMetadata,
    ) {
        val surrogate =
            MTOfflinePackMetadataSurrogate(
                value.id,
                value.state,
                value.size,
                value.createdAt,
                value.expiresAt,
                value.context,
                value.region,
                value.totalResources,
                value.totalTileResources,
                value.downloadedResources,
            )
        encoder.encodeSerializableValue(MTOfflinePackMetadataSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): MTOfflinePackMetadata {
        val surrogate = decoder.decodeSerializableValue(MTOfflinePackMetadataSurrogate.serializer())
        val expiresAt =
            surrogate.expiresAt
                ?: surrogate.createdAt.plusMillis(MTOfflineConfiguration.shared.defaultExpirationInterval)
        return MTOfflinePackMetadata(
            surrogate.id,
            surrogate.state,
            surrogate.size,
            surrogate.createdAt,
            expiresAt,
            surrogate.context,
            surrogate.region,
            surrogate.totalResources,
            surrogate.totalTileResources,
            surrogate.downloadedResources,
        )
    }
}
