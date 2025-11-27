/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.circle

import com.maptiler.maptilersdk.map.style.dsl.PropertyValue
import com.maptiler.maptilersdk.map.style.dsl.StyleValue
import com.maptiler.maptilersdk.map.style.dsl.StyleValueSerializer
import com.maptiler.maptilersdk.map.style.dsl.toJsonElement
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import com.maptiler.maptilersdk.map.style.layer.MTLayerVisibility
import com.maptiler.maptilersdk.map.style.layer.fill.ColorAsHexSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Circle style layer that renders filled circles.
 */
@Serializable
class MTCircleLayer : MTLayer {
    /** Unique layer identifier. */
    @SerialName("id")
    override var identifier: String

    /** Type of the layer. */
    override var type: MTLayerType = MTLayerType.CIRCLE
        private set

    /** Identifier of the source to be used for this layer. */
    @SerialName("source")
    override var sourceIdentifier: String

    /** The maximum zoom level for the layer. */
    @SerialName("maxzoom")
    override var maxZoom: Double? = null

    /** The minimum zoom level for the layer. */
    @SerialName("minzoom")
    override var minZoom: Double? = null

    /** Vector tile source layer name, if applicable. */
    @SerialName("source-layer")
    override var sourceLayer: String? = null

    // Paint

    /** Amount to blur the circle. */
    var blur: Double?
        get() = _paint.blur ?: 0.0
        set(value) {
            _paint.blur = value
        }

    /** The color of the circle (constant or expression). */
    var color: StyleValue?
        get() = _paint.color
        set(value) {
            _paint.color = value
        }

    /** The opacity of the circle. */
    var opacity: Double?
        get() = _paint.opacity ?: 1.0
        set(value) {
            _paint.opacity = value
        }

    /** The radius of the circle in pixels (constant or expression). */
    var radius: StyleValue?
        get() = _paint.radius
        set(value) {
            _paint.radius = value
        }

    /** The stroke color of the circle. */
    @Serializable(with = ColorAsHexSerializer::class)
    var strokeColor: Int?
        get() = _paint.strokeColor ?: null
        set(value) {
            _paint.strokeColor = value
        }

    /** The stroke opacity of the circle. */
    var strokeOpacity: Double?
        get() = _paint.strokeOpacity ?: 1.0
        set(value) {
            _paint.strokeOpacity = value
        }

    /** The stroke width of the circle. */
    var strokeWidth: Double?
        get() = _paint.strokeWidth ?: 0.0
        set(value) {
            _paint.strokeWidth = value
        }

    /** The geometry’s offset. Values are [x, y]. */
    var translate: DoubleArray?
        get() = _paint.translate ?: doubleArrayOf(0.0, 0.0)
        set(value) {
            _paint.translate = value
        }

    /** Controls the frame of reference for translate. */
    var translateAnchor: MTCircleTranslateAnchor?
        get() = _paint.translateAnchor ?: MTCircleTranslateAnchor.MAP
        set(value) {
            _paint.translateAnchor = value
        }

    /** Controls the alignment of circles when map is pitched. */
    var pitchAlignment: MTCirclePitchAlignment?
        get() = _paint.pitchAlignment ?: MTCirclePitchAlignment.VIEWPORT
        set(value) {
            _paint.pitchAlignment = value
        }

    /** Controls the scaling of circles when map is pitched. */
    var pitchScale: MTCirclePitchScale?
        get() = _paint.pitchScale ?: MTCirclePitchScale.MAP
        set(value) {
            _paint.pitchScale = value
        }

    // Layout

    /** Sort key to determine rendering order. */
    var sortKey: Double?
        get() = _layout.sortKey
        set(value) {
            _layout.sortKey = value
        }

    /** Visibility of the layer. */
    var visibility: MTLayerVisibility
        get() = MTLayerVisibility.from(_layout.visibility) ?: MTLayerVisibility.VISIBLE
        set(value) {
            _layout.visibility = value
        }

    @Suppress("PropertyName")
    @SerialName("layout")
    private var _layout: MTCircleLayout = MTCircleLayout()

    @Suppress("PropertyName")
    @SerialName("paint")
    private var _paint: MTCirclePaint = MTCirclePaint()

    @Suppress("PropertyName")
    @SerialName("filter")
    private var _filter: JsonElement? = null

    // Inline filter DSL
    fun withFilter(expr: PropertyValue) {
        _filter = expr.toJsonElement()
    }

    constructor(
        identifier: String,
        sourceIdentifier: String,
    ) {
        this.identifier = identifier
        this.sourceIdentifier = sourceIdentifier
        this._layout = MTCircleLayout(visibility = visibility)
        this._paint =
            MTCirclePaint(
                blur = blur,
                color = color,
                opacity = opacity,
                radius = radius,
                strokeColor = strokeColor,
                strokeOpacity = strokeOpacity,
                strokeWidth = strokeWidth,
                translate = translate,
                translateAnchor = translateAnchor,
                pitchAlignment = pitchAlignment,
                pitchScale = pitchScale,
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
        this._layout = MTCircleLayout(visibility = visibility)
        this._paint =
            MTCirclePaint(
                blur = blur,
                color = color,
                opacity = opacity,
                radius = radius,
                strokeColor = strokeColor,
                strokeOpacity = strokeOpacity,
                strokeWidth = strokeWidth,
                translate = translate,
                translateAnchor = translateAnchor,
                pitchAlignment = pitchAlignment,
                pitchScale = pitchScale,
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
        color: StyleValue?,
        opacity: Double?,
        radius: StyleValue?,
        strokeColor: Int?,
        strokeOpacity: Double?,
        strokeWidth: Double?,
        translate: DoubleArray?,
        translateAnchor: MTCircleTranslateAnchor?,
        pitchAlignment: MTCirclePitchAlignment?,
        pitchScale: MTCirclePitchScale?,
        sortKey: Double?,
        visibility: MTLayerVisibility,
    ) {
        this.identifier = identifier
        this.type = type
        this.sourceIdentifier = sourceIdentifier
        this.maxZoom = maxZoom
        this.minZoom = minZoom
        this.sourceLayer = sourceLayer
        this.blur = blur
        this.color = color
        this.opacity = opacity
        this.radius = radius
        this.strokeColor = strokeColor
        this.strokeOpacity = strokeOpacity
        this.strokeWidth = strokeWidth
        this.translate = translate
        this.translateAnchor = translateAnchor
        this.pitchAlignment = pitchAlignment
        this.pitchScale = pitchScale
        this.sortKey = sortKey
        this.visibility = visibility
        this._layout =
            MTCircleLayout(
                sortKey = sortKey,
                visibility = visibility,
            )
        this._paint =
            MTCirclePaint(
                blur = blur,
                color = color,
                opacity = opacity,
                radius = radius,
                strokeColor = strokeColor,
                strokeOpacity = strokeOpacity,
                strokeWidth = strokeWidth,
                translate = translate,
                translateAnchor = translateAnchor,
                pitchAlignment = pitchAlignment,
                pitchScale = pitchScale,
            )
    }
}

@Serializable
internal data class MTCircleLayout(
    @SerialName("circle-sort-key")
    var sortKey: Double? = null,
    var visibility: MTLayerVisibility = MTLayerVisibility.VISIBLE,
)

@Serializable
internal data class MTCirclePaint(
    @SerialName("circle-blur")
    var blur: Double? = 0.0,
    @SerialName("circle-color")
    @Serializable(with = StyleValueSerializer::class)
    var color: StyleValue? = null,
    @SerialName("circle-opacity")
    var opacity: Double? = 1.0,
    @SerialName("circle-radius")
    @Serializable(with = StyleValueSerializer::class)
    var radius: StyleValue? = null,
    @Serializable(with = ColorAsHexSerializer::class)
    @SerialName("circle-stroke-color")
    var strokeColor: Int? = null,
    @SerialName("circle-stroke-opacity")
    var strokeOpacity: Double? = 1.0,
    @SerialName("circle-stroke-width")
    var strokeWidth: Double? = 0.0,
    @SerialName("circle-translate")
    var translate: DoubleArray? = doubleArrayOf(0.0, 0.0),
    @SerialName("circle-translate-anchor")
    var translateAnchor: MTCircleTranslateAnchor? = MTCircleTranslateAnchor.MAP,
    @SerialName("circle-pitch-alignment")
    var pitchAlignment: MTCirclePitchAlignment? = MTCirclePitchAlignment.VIEWPORT,
    @SerialName("circle-pitch-scale")
    var pitchScale: MTCirclePitchScale? = MTCirclePitchScale.MAP,
)

// DSL helpers for inline configuration

fun MTCircleLayer.colorConst(color: Int) =
    apply {
        this.color = StyleValue.Color(color)
    }

fun MTCircleLayer.colorExpr(expr: PropertyValue) =
    apply {
        this.color = StyleValue.Expression(expr)
    }

fun MTCircleLayer.radiusConst(value: Double) =
    apply {
        this.radius = StyleValue.Number(value)
    }

fun MTCircleLayer.radiusExpr(expr: PropertyValue) =
    apply {
        this.radius = StyleValue.Expression(expr)
    }
