/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.line

import android.graphics.Color
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import com.maptiler.maptilersdk.map.style.layer.MTLayerVisibility
import com.maptiler.maptilersdk.map.style.layer.fill.ColorAsHexSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MTLineLayer : MTLayer {
    /**
     * Unique layer identifier.
     */
    @SerialName("id")
    override var identifier: String

    /**
     * Type of the layer.
     */
    override var type: MTLayerType = MTLayerType.LINE
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
     * Blur of the line.
     */
    var blur: Double?
        get() = _paint.blur ?: 0.0
        set(value) {
            _paint.blur = value
        }

    /**
     * The lengths of the alternating dashes and gaps that form the dash pattern.
     */
    var dashArray: DoubleArray?
        get() = _paint.dashArray ?: null
        set(value) {
            _paint.dashArray = value
        }

    /**
     * Line casing outside of a line’s actual path.
     */
    var gapWidth: Double?
        get() = _paint.gapWidth ?: 0.0
        set(value) {
            _paint.gapWidth = value
        }

    /**
     * The color with which the line will be drawn.
     */
    @Serializable(with = ColorAsHexSerializer::class)
    var color: Int?
        get() = _paint.color ?: Color.BLACK
        set(value) {
            _paint.color = value
        }

    /**
     * The gradient with which to color a line feature.
     * Can only be used with GeoJSON sources that specify "lineMetrics": true.
     */
    @Serializable(with = ColorAsHexSerializer::class)
    var gradient: Int?
        get() = _paint.gradient ?: null
        set(value) {
            _paint.gradient = value
        }

    /**
     * The line’s offset.
     */
    var offset: Double?
        get() = _paint.offset ?: 0.0
        set(value) {
            _paint.offset = value
        }

    /**
     * Optional number between 0 and 1 inclusive.
     */
    var opacity: Double?
        get() = _paint.opacity ?: 1.0
        set(value) {
            _paint.opacity = value
        }

    /**
     * The geometry’s offset. Values are [x, y].
     */
    var translate: DoubleArray?
        get() = _paint.translate ?: doubleArrayOf(0.0, 0.0)
        set(value) {
            _paint.translate = value
        }

    /**
     * Controls the frame of reference for translate.
     */
    var translateAnchor: MTLineTranslateAnchor?
        get() = _paint.translateAnchor ?: MTLineTranslateAnchor.MAP
        set(value) {
            _paint.translateAnchor = value
        }

    /**
     * Width of the line.
     */
    var width: Double?
        get() = _paint.width ?: 1.0
        set(value) {
            _paint.width = value
        }

    /**
     * The display of line endings.
     */
    var cap: MTLineCap
        get() = _layout.cap ?: MTLineCap.BUTT
        set(value) {
            _layout.cap = value
        }

    /**
     * The display of lines when joining.
     */
    var join: MTLineJoin
        get() = _layout.join ?: MTLineJoin.MITER
        set(value) {
            _layout.join = value
        }

    /**
     * Used to automatically convert miter joins to bevel joins for sharp angles. Requires join to be "miter".
     */
    var miterLimit: Double?
        get() = _layout.miterLimit ?: 2.0
        set(value) {
            _layout.miterLimit = value
        }

    /**
     * Used to automatically convert round joins to miter joins for shallow angles.
     */
    var roundLimit: Double?
        get() = _layout.roundLimit ?: 1.05
        set(value) {
            _layout.roundLimit = value
        }

    /**
     * Feature ordering value.
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
    private var _layout: MTLineLayout = MTLineLayout()

    @Suppress("PropertyName")
    @SerialName("paint")
    private var _paint: MTLinePaint = MTLinePaint()

    constructor(
        identifier: String,
        sourceIdentifier: String,
    ) {
        this.identifier = identifier
        this.sourceIdentifier = sourceIdentifier

        this._layout = MTLineLayout(visibility = visibility)
        this._paint =
            MTLinePaint(
                blur = blur,
                dashArray = dashArray,
                gapWidth = gapWidth,
                color = color,
                gradient = gradient,
                offset = offset,
                opacity = opacity,
                translate = translate,
                translateAnchor = translateAnchor,
                width = width,
            )
    }

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

        this._layout = MTLineLayout(visibility = visibility)
        this._paint =
            MTLinePaint(
                blur = blur,
                dashArray = dashArray,
                gapWidth = gapWidth,
                color = color,
                gradient = gradient,
                offset = offset,
                opacity = opacity,
                translate = translate,
                translateAnchor = translateAnchor,
                width = width,
            )
    }

    constructor(
        identifier: String,
        type: MTLayerType,
        sourceIdentifier: String,
        maxZoom: Double?,
        minZoom: Double?,
        sourceLayer: String?,
        blur: Double?,
        cap: MTLineCap?,
        color: Int?,
        dashArray: DoubleArray?,
        gapWidth: Double?,
        gradient: Int?,
        join: MTLineJoin?,
        miterLimit: Double?,
        offset: Double?,
        opacity: Double?,
        roundLimit: Double?,
        sortKey: Double?,
        translate: DoubleArray?,
        translateAnchor: MTLineTranslateAnchor?,
        width: Double?,
        visibility: MTLayerVisibility,
    ) {
        this.identifier = identifier
        this.type = type
        this.sourceIdentifier = sourceIdentifier
        this.maxZoom = maxZoom
        this.minZoom = minZoom
        this.sourceLayer = sourceLayer
        this.blur = blur
        cap?.let { this.cap = it }
        this.color = color
        this.dashArray = dashArray
        this.gapWidth = gapWidth
        this.gradient = gradient
        join?.let { this.join = it }
        this.miterLimit = miterLimit
        this.offset = offset
        this.opacity = opacity
        this.roundLimit = roundLimit
        this.sortKey = sortKey
        this.translate = translate
        this.translateAnchor = translateAnchor
        this.width = width
        this.visibility = visibility

        this._layout =
            MTLineLayout(
                cap = cap,
                join = join,
                miterLimit = miterLimit,
                roundLimit = roundLimit,
                sortKey = sortKey,
                visibility = visibility,
            )
        this._paint =
            MTLinePaint(
                blur = blur,
                dashArray = dashArray,
                gapWidth = gapWidth,
                color = color,
                gradient = gradient,
                offset = offset,
                opacity = opacity,
                translate = translate,
                translateAnchor = translateAnchor,
                width = width,
            )
    }
}

@Serializable
internal data class MTLineLayout(
    @SerialName("line-cap")
    var cap: MTLineCap? = MTLineCap.BUTT,
    @SerialName("line-join")
    var join: MTLineJoin? = MTLineJoin.MITER,
    @SerialName("line-miter-limit")
    var miterLimit: Double? = 2.0,
    @SerialName("line-round-limit")
    var roundLimit: Double? = 1.05,
    @SerialName("line-sort-key")
    var sortKey: Double? = null,
    var visibility: MTLayerVisibility = MTLayerVisibility.VISIBLE,
)

@Serializable
internal data class MTLinePaint(
    @SerialName("line-blur")
    var blur: Double? = 0.0,
    @SerialName("line-dasharray")
    var dashArray: DoubleArray? = null,
    @SerialName("line-gap-width")
    var gapWidth: Double? = 0.0,
    @Serializable(with = ColorAsHexSerializer::class)
    @SerialName("line-color")
    var color: Int? = Color.BLACK,
    @Serializable(with = ColorAsHexSerializer::class)
    @SerialName("line-gradient")
    var gradient: Int? = null,
    @SerialName("line-offset")
    var offset: Double? = 0.0,
    @SerialName("line-opacity")
    var opacity: Double? = 1.0,
    @SerialName("line-translate")
    var translate: DoubleArray? = doubleArrayOf(0.0, 0.0),
    @SerialName("line-translate-anchor")
    var translateAnchor: MTLineTranslateAnchor? = MTLineTranslateAnchor.MAP,
    @SerialName("line-width")
    var width: Double? = 1.0,
)
