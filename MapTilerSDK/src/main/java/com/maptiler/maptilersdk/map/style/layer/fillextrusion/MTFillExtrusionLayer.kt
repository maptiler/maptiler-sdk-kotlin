/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.fillextrusion

import android.graphics.Color
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
 * A fill-extrusion style layer renders one or more filled (and optionally stroked) extruded (3D) polygons on a map.
 *
 * You can use a fill-extrusion layer to configure the extrusion and visual appearance of polygon or multipolygon
 * features.
 */
@Serializable
class MTFillExtrusionLayer : MTLayer {
    /**
     * Unique layer identifier.
     */
    @SerialName("id")
    override var identifier: String

    /**
     * Type of the layer.
     */
    override var type: MTLayerType = MTLayerType.FILL_EXTRUSION
        private set

    /**
     * Identifier of the source to be used for this layer.
     */
    @SerialName("source")
    override var sourceIdentifier: String

    /**
     * The maximum zoom level for the layer. Optional number between 0 and 24.
     */
    @SerialName("maxzoom")
    override var maxZoom: Double? = null

    /**
     * The minimum zoom level for the layer. Optional number between 0 and 24.
     */
    @SerialName("minzoom")
    override var minZoom: Double? = null

    /**
     * Vector tile source layer name, if applicable. Required for vector tile sources.
     */
    @SerialName("source-layer")
    override var sourceLayer: String? = null

    // Paint

    /**
     * The height with which to extrude the base of this layer. Units in meters. Must be ≤ height.
     * Defaults to 0. Requires [height].
     */
    var base: Double?
        get() = (_paint.base as? StyleValue.Number)?.value
        set(value) {
            _paint.base = value?.let { StyleValue.Number(it) }
        }

    /**
     * The base color of the extruded fill. Defaults to black.
     * Note: If specified as rgba with alpha, alpha is ignored; use [opacity].
     */
    @Serializable(with = ColorAsHexSerializer::class)
    var color: Int?
        get() = (_paint.color as? StyleValue.Color)?.value
        set(value) {
            _paint.color = value?.let { StyleValue.Color(it) }
        }

    /**
     * The height with which to extrude this layer. Units in meters. Defaults to 0.
     */
    var height: Double?
        get() = (_paint.height as? StyleValue.Number)?.value
        set(value) {
            _paint.height = value?.let { StyleValue.Number(it) }
        }

    /**
     * The opacity of the entire fill extrusion layer. Optional number between 0 and 1 inclusive. Defaults to 1.
     */
    var opacity: Double?
        get() = (_paint.opacity as? StyleValue.Number)?.value
        set(value) {
            _paint.opacity = value?.let { StyleValue.Number(it) }
        }

    /**
     * Name of image in sprite to use for drawing images on extruded fills.
     */
    var pattern: String?
        get() = _paint.pattern
        set(value) {
            _paint.pattern = value
        }

    /**
     * The geometry’s offset. Units in pixels. Values are [x, y] where negatives indicate left and up, respectively.
     * Defaults to [0,0].
     */
    var translate: DoubleArray?
        get() = _paint.translate ?: doubleArrayOf(0.0, 0.0)
        set(value) {
            _paint.translate = value
        }

    /**
     * Controls the frame of reference for translate. Defaults to map.
     */
    var translateAnchor: MTFillExtrusionTranslateAnchor?
        get() = _paint.translateAnchor ?: MTFillExtrusionTranslateAnchor.MAP
        set(value) {
            _paint.translateAnchor = value
        }

    /**
     * Whether to apply a vertical gradient to the sides of a fill-extrusion layer. Defaults to true.
     */
    var verticalGradient: Boolean?
        get() = _paint.verticalGradient ?: true
        set(value) {
            _paint.verticalGradient = value
        }

    // Layout

    /** Whether this layer is displayed. */
    var visibility: MTLayerVisibility
        get() = MTLayerVisibility.from(_layout.visibility) ?: MTLayerVisibility.VISIBLE
        set(value) {
            _layout.visibility = value
        }

    @SerialName("layout")
    @Suppress("PropertyName")
    private var _layout: MTFillExtrusionLayout = MTFillExtrusionLayout()

    @SerialName("paint")
    @Suppress("PropertyName")
    private var _paint: MTFillExtrusionPaint = MTFillExtrusionPaint()

    @SerialName("filter")
    @Suppress("PropertyName")
    private var _filter: JsonElement? = null

    fun withFilter(expr: PropertyValue) {
        _filter = expr.toJsonElement()
    }

    // --- DSL helpers (member functions) ---
    fun baseConst(value: Double) = apply { this.base = value }

    fun baseExpr(expr: PropertyValue) = apply { this._paint.base = StyleValue.Expression(expr) }

    fun colorConst(color: Int) = apply { this.color = color }

    fun colorExpr(expr: PropertyValue) = apply { this._paint.color = StyleValue.Expression(expr) }

    fun heightConst(value: Double) = apply { this.height = value }

    fun heightExpr(expr: PropertyValue) = apply { this._paint.height = StyleValue.Expression(expr) }

    fun opacityConst(value: Double) = apply { this.opacity = value }

    fun opacityExpr(expr: PropertyValue) = apply { this._paint.opacity = StyleValue.Expression(expr) }

    constructor(
        identifier: String,
        sourceIdentifier: String,
    ) {
        this.identifier = identifier
        this.sourceIdentifier = sourceIdentifier
        this._layout = MTFillExtrusionLayout(visibility = visibility)
        this._paint =
            MTFillExtrusionPaint(
                base = base?.let { StyleValue.Number(it) },
                color = color?.let { StyleValue.Color(it) },
                height = height?.let { StyleValue.Number(it) },
                opacity = opacity?.let { StyleValue.Number(it) },
                pattern = pattern,
                translate = translate,
                translateAnchor = translateAnchor,
                verticalGradient = verticalGradient,
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
        this._layout = MTFillExtrusionLayout(visibility = visibility)
        this._paint =
            MTFillExtrusionPaint(
                base = base?.let { StyleValue.Number(it) },
                color = color?.let { StyleValue.Color(it) },
                height = height?.let { StyleValue.Number(it) },
                opacity = opacity?.let { StyleValue.Number(it) },
                pattern = pattern,
                translate = translate,
                translateAnchor = translateAnchor,
                verticalGradient = verticalGradient,
            )
    }

    constructor(
        identifier: String,
        type: MTLayerType,
        sourceIdentifier: String,
        maxZoom: Double?,
        minZoom: Double?,
        sourceLayer: String?,
        base: Double?,
        color: Int?,
        height: Double?,
        opacity: Double?,
        pattern: String?,
        translate: DoubleArray?,
        translateAnchor: MTFillExtrusionTranslateAnchor?,
        verticalGradient: Boolean?,
        visibility: MTLayerVisibility,
    ) {
        this.identifier = identifier
        this.type = type
        this.sourceIdentifier = sourceIdentifier
        this.maxZoom = maxZoom
        this.minZoom = minZoom
        this.sourceLayer = sourceLayer
        this.base = base
        this.color = color
        this.height = height
        this.opacity = opacity
        this.pattern = pattern
        this.translate = translate
        this.translateAnchor = translateAnchor
        this.verticalGradient = verticalGradient
        this.visibility = visibility
        this._layout =
            MTFillExtrusionLayout(
                visibility = visibility,
            )
        this._paint =
            MTFillExtrusionPaint(
                base = base?.let { StyleValue.Number(it) },
                color = color?.let { StyleValue.Color(it) },
                height = height?.let { StyleValue.Number(it) },
                opacity = opacity?.let { StyleValue.Number(it) },
                pattern = pattern,
                translate = translate,
                translateAnchor = translateAnchor,
                verticalGradient = verticalGradient,
            )
    }
}

@Serializable
internal data class MTFillExtrusionLayout(
    var visibility: MTLayerVisibility = MTLayerVisibility.VISIBLE,
)

@Serializable
internal data class MTFillExtrusionPaint(
    @SerialName("fill-extrusion-base")
    @Serializable(with = StyleValueSerializer::class)
    var base: StyleValue? = null,
    @SerialName("fill-extrusion-color")
    @Serializable(with = StyleValueSerializer::class)
    var color: StyleValue? = null,
    @SerialName("fill-extrusion-height")
    @Serializable(with = StyleValueSerializer::class)
    var height: StyleValue? = null,
    @SerialName("fill-extrusion-opacity")
    @Serializable(with = StyleValueSerializer::class)
    var opacity: StyleValue? = null,
    @SerialName("fill-extrusion-pattern")
    var pattern: String? = null,
    @SerialName("fill-extrusion-translate")
    var translate: DoubleArray? = doubleArrayOf(0.0, 0.0),
    @SerialName("fill-extrusion-translate-anchor")
    var translateAnchor: MTFillExtrusionTranslateAnchor? = MTFillExtrusionTranslateAnchor.MAP,
    @SerialName("fill-extrusion-vertical-gradient")
    var verticalGradient: Boolean? = true,
)
