/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.URL

/**
 * Defines a region for offline download.
 */
@Serializable(with = MTOfflineRegionDefinitionSerializer::class)
data class MTOfflineRegionDefinition(
    /**
     * The geometry of the region.
     */
    val geometry: MTOfflineRegionGeometry,
    /**
     * The minimum zoom level.
     */
    val minZoom: Int,
    /**
     * The maximum zoom level.
     */
    val maxZoom: Int,
    /**
     * The reference style for the map.
     */
    val referenceStyle: MTMapReferenceStyle,
    /**
     * The optional style variant.
     */
    val styleVariant: MTMapStyleVariant? = null,
    /**
     * The device pixel ratio.
     */
    val pixelRatio: Float = 1.0f,
    /**
     * The maximum number of tiles allowed for this region.
     */
    val maxTileCount: Int? = null,
    /**
     * An optional buffer in meters to add around the geometry for map interaction and tile fetching.
     */
    val padding: Double? = null,
) {
    /**
     * The bounding box of the region.
     */
    val bbox: MTBoundingBox
        get() = geometry.bbox

    /**
     * Secondary constructor for bounding box.
     */
    constructor(
        bbox: MTBoundingBox,
        minZoom: Int,
        maxZoom: Int,
        referenceStyle: MTMapReferenceStyle,
        styleVariant: MTMapStyleVariant? = null,
        pixelRatio: Float = 1.0f,
        maxTileCount: Int? = null,
        padding: Double? = null,
    ) : this(
        geometry = MTOfflineRegionGeometry.BoundingBox(bbox),
        minZoom = minZoom,
        maxZoom = maxZoom,
        referenceStyle = referenceStyle,
        styleVariant = styleVariant,
        pixelRatio = pixelRatio,
        maxTileCount = maxTileCount,
        padding = padding,
    )
}

internal object MTOfflineRegionDefinitionSerializer : KSerializer<MTOfflineRegionDefinition> {
    override val descriptor: SerialDescriptor = DefinitionSurrogate.serializer().descriptor

    override fun serialize(
        encoder: Encoder,
        value: MTOfflineRegionDefinition,
    ) {
        val surrogate =
            DefinitionSurrogate(
                geometry = value.geometry,
                bbox = value.bbox,
                minZoom = value.minZoom,
                maxZoom = value.maxZoom,
                referenceStyle = value.referenceStyle,
                styleVariant = value.styleVariant,
                pixelRatio = value.pixelRatio,
                maxTileCount = value.maxTileCount,
                padding = value.padding,
            )
        encoder.encodeSerializableValue(DefinitionSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): MTOfflineRegionDefinition {
        val surrogate = decoder.decodeSerializableValue(DefinitionSurrogate.serializer())

        val geometry =
            surrogate.geometry ?: surrogate.bbox?.let { MTOfflineRegionGeometry.BoundingBox(it) }
                ?: throw IllegalArgumentException("MTOfflineRegionDefinition must have either 'geometry' or 'bbox'")

        return MTOfflineRegionDefinition(
            geometry = geometry,
            minZoom = surrogate.minZoom,
            maxZoom = surrogate.maxZoom,
            referenceStyle = surrogate.referenceStyle,
            styleVariant = surrogate.styleVariant,
            pixelRatio = surrogate.pixelRatio,
            maxTileCount = surrogate.maxTileCount,
            padding = surrogate.padding,
        )
    }

    @Serializable
    private data class DefinitionSurrogate(
        val geometry: MTOfflineRegionGeometry? = null,
        val bbox: MTBoundingBox? = null,
        val minZoom: Int,
        val maxZoom: Int,
        @Serializable(with = MTMapReferenceStyleSerializer::class)
        val referenceStyle: MTMapReferenceStyle,
        @Serializable(with = MTMapStyleVariantSerializer::class)
        val styleVariant: MTMapStyleVariant? = null,
        val pixelRatio: Float = 1.0f,
        val maxTileCount: Int? = null,
        val padding: Double? = null,
    )
}

internal object MTMapReferenceStyleSerializer : KSerializer<MTMapReferenceStyle> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("MTMapReferenceStyle", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: MTMapReferenceStyle,
    ) {
        encoder.encodeString(value.getName())
    }

    override fun deserialize(decoder: Decoder): MTMapReferenceStyle {
        val name = decoder.decodeString()
        return try {
            val url = URL(name)
            MTMapReferenceStyle.CUSTOM(url)
        } catch (e: Exception) {
            MTMapReferenceStyle.all().find { it.getName() == name }
                ?: MTMapReferenceStyle.STREETS // Default fallback
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
internal object MTMapStyleVariantSerializer : KSerializer<MTMapStyleVariant?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("MTMapStyleVariant", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: MTMapStyleVariant?,
    ) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeString(value.value)
        }
    }

    override fun deserialize(decoder: Decoder): MTMapStyleVariant? {
        if (!decoder.decodeNotNullMark()) {
            return decoder.decodeNull()
        }
        val value = decoder.decodeString()
        return MTMapStyleVariant.entries.find { it.value == value }
    }
}
