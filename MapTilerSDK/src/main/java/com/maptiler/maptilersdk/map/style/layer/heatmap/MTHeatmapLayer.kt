/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.heatmap

import com.maptiler.maptilersdk.map.style.dsl.PropertyValue
import com.maptiler.maptilersdk.map.style.dsl.StyleValue
import com.maptiler.maptilersdk.map.style.dsl.StyleValueSerializer
import com.maptiler.maptilersdk.map.style.dsl.toJsonElement
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import com.maptiler.maptilersdk.map.style.layer.MTLayerVisibility
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * A heatmap style layer renders a range of colors to represent the density of points in an area.
 *
 * Supports color ramps and expressions for intensity, radius, opacity, and weight.
 */
@Serializable
class MTHeatmapLayer : MTLayer {
    /** Unique layer identifier. */
    @SerialName("id")
    override var identifier: String

    /** Type of the layer. */
    override var type: MTLayerType = MTLayerType.HEATMAP
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

    /**
     * Defines the color of each pixel based on its density value in a heatmap.
     * Should be an expression that uses ["heatmap-density"] as input.
     */
    var color: StyleValue?
        get() = _paint.color
        set(value) {
            _paint.color = value
        }

    /**
     * Similar to heatmap-weight but controls the intensity of the heatmap globally.
     * Primarily used for adjusting the heatmap based on zoom level.
     */
    var intensity: StyleValue?
        get() = _paint.intensity
        set(value) {
            _paint.intensity = value
        }

    /** The global opacity at which the heatmap layer will be drawn. */
    var opacity: StyleValue?
        get() = _paint.opacity
        set(value) {
            _paint.opacity = value
        }

    /**
     * Radius of influence of one heatmap point in pixels. Increasing the value
     * makes the heatmap smoother, but less detailed.
     */
    var radius: StyleValue?
        get() = _paint.radius
        set(value) {
            _paint.radius = value
        }

    /**
     * A measure of how much an individual point contributes to the heatmap.
     */
    var weight: StyleValue?
        get() = _paint.weight
        set(value) {
            _paint.weight = value
        }

    // Layout

    /** Whether this layer is displayed. */
    var visibility: MTLayerVisibility
        get() = MTLayerVisibility.from(_layout.visibility) ?: MTLayerVisibility.VISIBLE
        set(value) {
            _layout.visibility = value
        }

    @Suppress("PropertyName")
    @SerialName("layout")
    private var _layout: MTHeatmapLayout = MTHeatmapLayout()

    @Suppress("PropertyName")
    @SerialName("paint")
    private var _paint: MTHeatmapPaint = MTHeatmapPaint()

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
        this._layout = MTHeatmapLayout(visibility = visibility)
        this._paint =
            MTHeatmapPaint(
                color = color,
                intensity = intensity,
                opacity = opacity,
                radius = radius,
                weight = weight,
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

        this._layout = MTHeatmapLayout(visibility = visibility)
        this._paint =
            MTHeatmapPaint(
                color = color,
                intensity = intensity,
                opacity = opacity,
                radius = radius,
                weight = weight,
            )
    }

    constructor(
        identifier: String,
        type: MTLayerType,
        sourceIdentifier: String,
        maxZoom: Double?,
        minZoom: Double?,
        sourceLayer: String?,
        color: StyleValue?,
        intensity: StyleValue?,
        opacity: StyleValue?,
        radius: StyleValue?,
        weight: StyleValue?,
        visibility: MTLayerVisibility,
    ) {
        this.identifier = identifier
        this.type = type
        this.sourceIdentifier = sourceIdentifier
        this.maxZoom = maxZoom
        this.minZoom = minZoom
        this.sourceLayer = sourceLayer
        this.color = color
        this.intensity = intensity
        this.opacity = opacity
        this.radius = radius
        this.weight = weight
        this.visibility = visibility

        this._layout =
            MTHeatmapLayout(
                visibility = visibility,
            )
        this._paint =
            MTHeatmapPaint(
                color = color,
                intensity = intensity,
                opacity = opacity,
                radius = radius,
                weight = weight,
            )
    }
}

@Serializable
internal data class MTHeatmapLayout(
    var visibility: MTLayerVisibility = MTLayerVisibility.VISIBLE,
)

@Serializable
internal data class MTHeatmapPaint(
    @SerialName("heatmap-color")
    @Serializable(with = StyleValueSerializer::class)
    var color: StyleValue? = null,
    @SerialName("heatmap-intensity")
    @Serializable(with = StyleValueSerializer::class)
    var intensity: StyleValue? = StyleValue.Number(1.0),
    @SerialName("heatmap-opacity")
    @Serializable(with = StyleValueSerializer::class)
    var opacity: StyleValue? = StyleValue.Number(1.0),
    @SerialName("heatmap-radius")
    @Serializable(with = StyleValueSerializer::class)
    var radius: StyleValue? = StyleValue.Number(30.0),
    @SerialName("heatmap-weight")
    @Serializable(with = StyleValueSerializer::class)
    var weight: StyleValue? = StyleValue.Number(1.0),
)

// DSL helpers for inline configuration

fun MTHeatmapLayer.colorConst(color: Int) = apply { this.color = StyleValue.Color(color) }

fun MTHeatmapLayer.colorExpr(expr: PropertyValue) = apply { this.color = StyleValue.Expression(expr) }

fun MTHeatmapLayer.intensityConst(value: Double) = apply { this.intensity = StyleValue.Number(value) }

fun MTHeatmapLayer.intensityExpr(expr: PropertyValue) = apply { this.intensity = StyleValue.Expression(expr) }

fun MTHeatmapLayer.opacityConst(value: Double) = apply { this.opacity = StyleValue.Number(value) }

fun MTHeatmapLayer.opacityExpr(expr: PropertyValue) = apply { this.opacity = StyleValue.Expression(expr) }

fun MTHeatmapLayer.radiusConst(value: Double) = apply { this.radius = StyleValue.Number(value) }

fun MTHeatmapLayer.radiusExpr(expr: PropertyValue) = apply { this.radius = StyleValue.Expression(expr) }

fun MTHeatmapLayer.weightConst(value: Double) = apply { this.weight = StyleValue.Number(value) }

fun MTHeatmapLayer.weightExpr(expr: PropertyValue) = apply { this.weight = StyleValue.Expression(expr) }
