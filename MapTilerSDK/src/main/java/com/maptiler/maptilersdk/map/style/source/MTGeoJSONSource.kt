/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.source

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.URL

/**
 * A geojson source.
 */
@Serializable
class MTGeoJSONSource : MTSource {
    companion object {
        fun fromUrl(
            identifier: String,
            url: URL,
        ): MTGeoJSONSource = MTGeoJSONSource(identifier = identifier, url = url)

        fun fromJsonString(
            identifier: String,
            json: String,
        ): MTGeoJSONSource = MTGeoJSONSource(identifier = identifier, jsonString = json)
    }

    /**
     * Unique identifier of a source.
     */
    @SerialName("id")
    override var identifier: String

    /**
     * Attribution to be displayed when the map is shown to a user.
     */
    var attribution: String? = null

    /**
     * A URL to a GeoJSON resource. Supported protocols are http, https.
     */
    @Serializable(with = URLAsStringSerializer::class)
    @SerialName("data")
    override var url: URL? = null

    /**
     * Type of the layer.
     */
    override var type: MTSourceType = MTSourceType.GEOJSON
        private set

    /**
     * GeoJSON String.
     *
     * GeoJSON string parses coordinates as Longitude, Latitude pairs, in that order.
     */
    var jsonString: String? = null

    /**
     * Size of the tile buffer on each side.
     *
     * Optional number between 0 and 512 inclusive. A value of 0 produces no buffer.
     * A value of 512 produces a buffer as wide as the tile itself.
     * Larger values produce fewer rendering artifacts near tile edges and slower performance.
     * Defaults to 128.
     */
    var buffer: Int? = 128

    /**
     * If the data is a collection of point features, sets the points by radius into groups.
     *
     * Defaults to false.
     */
    @SerialName("cluster")
    var isCluster: Boolean = false

    /**
     * Max zoom on which to cluster points if clustering is enabled.
     *
     * Defaults to one zoom less than maxzoom (so that last zoom features are not clustered).
     */
    var clusterMaxZoom: Double? = null

    /**
     * Radius of each cluster if clustering is enabled.
     *
     * A value of 512 indicates a radius equal to the width of a tile.
     *
     * Defaults to 50.
     */
    var clusterRadius: Double? = 50.0

    /**
     * Maximum zoom level at which to create vector tiles.
     *
     * Higher value means greater detail at high zoom levels.
     * Defaults to 18.
     */
    @SerialName("maxzoom")
    var maxZoom: Double? = 18.0

    /**
     * Douglas-Peucker simplification tolerance.
     *
     * Higher value means simpler geometries and faster performance.
     *  Defaults to 0.375
     */
    var tolerance: Double? = 0.375

    /**
     * Specifies whether to calculate line distance metrics
     */
    var lineMetrics: Boolean? = false

    constructor(
        identifier: String,
        attribution: String?,
        url: URL?,
        jsonString: String?,
        buffer: Int?,
        isCluster: Boolean,
        clusterMaxZoom: Double?,
        clusterRadius: Double?,
        maxZoom: Double?,
        tolerance: Double?,
        lineMetrics: Boolean?,
    ) {
        this.identifier = identifier
        this.attribution = attribution
        this.url = url
        this.jsonString = jsonString
        this.buffer = buffer
        this.isCluster = isCluster
        this.clusterMaxZoom = clusterMaxZoom
        this.clusterRadius = clusterRadius
        this.maxZoom = maxZoom
        this.tolerance = tolerance
        this.lineMetrics = lineMetrics
    }

    constructor(
        identifier: String,
        url: URL,
    ) {
        this.identifier = identifier
        this.url = url
    }

    constructor(
        identifier: String,
        jsonString: String,
    ) {
        this.identifier = identifier
        this.jsonString = jsonString
    }
}

object URLAsStringSerializer : KSerializer<URL> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("URL", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: URL,
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): URL = URL(decoder.decodeString())
}
