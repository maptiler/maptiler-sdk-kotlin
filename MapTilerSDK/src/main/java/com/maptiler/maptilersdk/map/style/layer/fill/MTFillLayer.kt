/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.fill

import android.graphics.Color
import com.maptiler.maptilersdk.helpers.toHexString
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import com.maptiler.maptilersdk.map.style.layer.MTLayerVisibility
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * The fill style layer that renders one or more filled (and optionally stroked) polygons on a map.
 */
@Serializable
class MTFillLayer : MTLayer {
    /**
     * Unique layer identifier.
     */
    @SerialName("id")
    override var identifier: String

    /**
     * Type of the layer.
     */
    override var type: MTLayerType = MTLayerType.FILL
        private set

    /**
     * Identifier of the source to be used for this layer.
     */
    @SerialName("source")
    override var sourceIdentifier: String

    /**
     * The maximum zoom level for the layer.
     *
     * Optional number between 0 and 24. At zoom levels equal to or greater than the maxzoom,
     * the layer will be hidden.
     */
    @SerialName("maxzoom")
    override var maxZoom: Double? = null

    /**
     * The minimum zoom level for the layer.
     *
     * Optional number between 0 and 24. At zoom levels less than the minzoom,
     * the layer will be hidden.
     */
    @SerialName("minzoom")
    override var minZoom: Double? = null

    /**
     * Layer to use from a vector tile source.
     *
     * Required for vector tile sources; prohibited for all other source types,
     * including GeoJSON sources.
     */
    @SerialName("source-layer")
    override var sourceLayer: String? = null

    /**
     * Boolean indicating whether or not the fill should be antialiased.
     *
     * Defaults to true.
     */
    var shouldBeAntialised: Boolean?
        get() = _paint.shouldBeAntialised ?: true
        set(value) {
            _paint.shouldBeAntialised = value
        }

    /**
     * The color of the filled part of this layer.
     *
     * Defaults to black.
     */
    @Serializable(with = ColorAsHexSerializer::class)
    var color: Int?
        get() = _paint.color ?: Color.BLACK
        set(value) {
            _paint.color = value
        }

    /**
     * The opacity of the entire fill layer.
     *
     * Optional number between 0 and 1 inclusive.
     * Defaults to 1.
     */
    var opacity: Double?
        get() = _paint.opacity ?: 1.0
        set(value) {
            _paint.opacity = value
        }

    /**
     * The outline color of the fill.
     *
     * Matches the value of fill-color if unspecified.
     */
    @Serializable(with = ColorAsHexSerializer::class)
    var outlineColor: Int?
        get() = _paint.outlineColor ?: null
        set(value) {
            _paint.outlineColor = value
        }

    /**
     * The geometry’s offset.
     *
     * Units in pixels. Values are [x, y] where negatives indicate left and up, respectively.
     * Defaults to [0,0].
     */
    var translate: DoubleArray?
        get() = _paint.translate ?: doubleArrayOf(0.0, 0.0)
        set(value) {
            _paint.translate = value
        }

    /**
     * Enum controlling the frame of reference for translate.
     */
    var translateAnchor: MTFillTranslateAnchor?
        get() = _paint.translateAnchor ?: MTFillTranslateAnchor.MAP
        set(value) {
            _paint.translateAnchor = value
        }

    /**
     * Key for sorting features.
     *
     * Features with a higher sort key will appear above features with a lower sort key.
     */
    var sortKey: Double?
        get() = _layout.sortKey ?: null
        set(value) {
            _layout.sortKey = value
        }

    /**
     * Enum controlling whether this layer is displayed.
     */
    var visibility: MTLayerVisibility
        get() = MTLayerVisibility.from(_layout.visibility) ?: MTLayerVisibility.VISIBLE
        set(value) {
            _layout.visibility = value
        }

    @Suppress("PropertyName")
    @SerialName("layout")
    private var _layout: MTFillLayout = MTFillLayout()

    @Suppress("PropertyName")
    @SerialName("paint")
    private var _paint: MTPaintLayout = MTPaintLayout()

    constructor(
        identifier: String,
        sourceIdentifier: String,
        maxZoom: Double,
        minZoom: Double,
        sourceLayer: String,
    ) {
        this.identifier = identifier
        this.sourceIdentifier = sourceIdentifier
        this.maxZoom = maxZoom
        this.minZoom = minZoom
        this.sourceLayer = sourceLayer

        this._layout = MTFillLayout(visibility)
        this._paint = MTPaintLayout(color, shouldBeAntialised, opacity, outlineColor, translate, translateAnchor)
    }

    constructor(
        identifier: String,
        sourceIdentifier: String,
    ) {
        this.identifier = identifier
        this.sourceIdentifier = sourceIdentifier

        this._layout = MTFillLayout(visibility)
        this._paint = MTPaintLayout(color, shouldBeAntialised, opacity, outlineColor, translate, translateAnchor)
    }

    constructor(
        identifier: String,
        type: MTLayerType,
        sourceIdentifier: String,
        maxZoom: Double?,
        minZoom: Double?,
        sourceLayer: String?,
        shouldBeAntialised: Boolean?,
        color: Int?,
        opacity: Double?,
        outlineColor: Int?,
        translate: DoubleArray?,
        translateAnchor: MTFillTranslateAnchor?,
        sortKey: Double?,
        visibility: MTLayerVisibility,
    ) {
        this.identifier = identifier
        this.type = type
        this.sourceIdentifier = sourceIdentifier
        this.maxZoom = maxZoom
        this.minZoom = minZoom
        this.sourceLayer = sourceLayer
        this.shouldBeAntialised = shouldBeAntialised
        this.color = color
        this.opacity = opacity
        this.outlineColor = outlineColor
        this.translate = translate
        this.translateAnchor = translateAnchor
        this.sortKey = sortKey
        this.visibility = visibility
        this._layout = MTFillLayout(visibility)
        this._paint = MTPaintLayout(color, shouldBeAntialised, opacity, outlineColor, translate, translateAnchor)
    }
}

@Serializable
internal data class MTFillLayout(
    var visibility: MTLayerVisibility = MTLayerVisibility.VISIBLE,
    var sortKey: Double? = null,
)

@Serializable
internal data class MTPaintLayout(
    @Serializable(with = ColorAsHexSerializer::class)
    @SerialName("fill-color")
    var color: Int? = Color.BLACK,
    @SerialName("fill-antialias")
    var shouldBeAntialised: Boolean? = null,
    @SerialName("fill-opacity")
    var opacity: Double? = 1.0,
    @Serializable(with = ColorAsHexSerializer::class)
    @SerialName("fill-outline-color")
    var outlineColor: Int? = null,
    @SerialName("fill-translate")
    var translate: DoubleArray? = doubleArrayOf(0.0, 0.0),
    @SerialName("fill-translate-anchor")
    var translateAnchor: MTFillTranslateAnchor? = MTFillTranslateAnchor.MAP,
)

@Serializer(forClass = Int::class)
object ColorAsHexSerializer : KSerializer<Int> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ColorAsHex", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Int,
    ) {
        encoder.encodeString(value.toHexString())
    }

    override fun deserialize(decoder: Decoder): Int {
        val hex = decoder.decodeString()

        return hex.removePrefix("#").toInt(16)
    }
}
