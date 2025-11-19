/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.raster

import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import com.maptiler.maptilersdk.map.style.layer.MTLayerVisibility
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The raster style layer that renders raster map textures such as satellite imagery.
 *
 * Supports paint properties like brightness, contrast, fade duration, hue rotation,
 * opacity, resampling and saturation, and a layout visibility flag.
 */
@Serializable
class MTRasterLayer : MTLayer {
    /**
     * Unique layer identifier.
     */
    @SerialName("id")
    override var identifier: String

    /**
     * Type of the layer.
     */
    override var type: MTLayerType = MTLayerType.RASTER
        private set

    /**
     * Identifier of the source to be used for this layer.
     */
    @SerialName("source")
    override var sourceIdentifier: String

    /**
     * The maximum zoom level for the layer.
     * Optional number between 0 and 24. At zoom levels equal to or greater than the maxzoom,
     * the layer will be hidden.
     */
    @SerialName("maxzoom")
    override var maxZoom: Double? = null

    /**
     * The minimum zoom level for the layer.
     * Optional number between 0 and 24. At zoom levels less than the minzoom,
     * the layer will be hidden.
     */
    @SerialName("minzoom")
    override var minZoom: Double? = null

    /**
     * Layer to use from a vector tile source. Not used for raster sources.
     */
    @SerialName("source-layer")
    override var sourceLayer: String? = null

    /**
     * Increase or reduce the brightness of the image. The value is the maximum brightness.
     * Optional number between 0 and 1 inclusive. Defaults to 1.
     */
    var brightnessMax: Double?
        get() = _paint.brightnessMax ?: 1.0
        set(value) {
            _paint.brightnessMax = value
        }

    /**
     * Increase or reduce the brightness of the image. The value is the minimum brightness.
     * Optional number between 0 and 1 inclusive. Defaults to 0.
     */
    var brightnessMin: Double?
        get() = _paint.brightnessMin ?: 0.0
        set(value) {
            _paint.brightnessMin = value
        }

    /**
     * Increase or reduce the contrast of the image.
     * Optional number between -1 and 1 inclusive. Defaults to 0.
     */
    var contrast: Double?
        get() = _paint.contrast ?: 0.0
        set(value) {
            _paint.contrast = value
        }

    /**
     * Fade duration when a new tile is added.
     * Optional number greater than or equal to 0. Units in milliseconds. Defaults to 300.
     */
    var fadeDuration: Double?
        get() = _paint.fadeDuration ?: 300.0
        set(value) {
            _paint.fadeDuration = value
        }

    /**
     * Rotates hues around the color wheel. Units in degrees. Defaults to 0.
     */
    var hueRotate: Double?
        get() = _paint.hueRotate ?: 0.0
        set(value) {
            _paint.hueRotate = value
        }

    /**
     * The opacity at which the image will be drawn. Optional number between 0 and 1 inclusive. Defaults to 1.
     */
    var opacity: Double?
        get() = _paint.opacity ?: 1.0
        set(value) {
            _paint.opacity = value
        }

    /**
     * The resampling/interpolation method to use for overscaling. Defaults to LINEAR.
     */
    var resampling: MTRasterResampling?
        get() = _paint.resampling ?: MTRasterResampling.LINEAR
        set(value) {
            _paint.resampling = value
        }

    /**
     * Increase or reduce the saturation of the image. Optional number between -1 and 1 inclusive. Defaults to 0.
     */
    var saturation: Double?
        get() = _paint.saturation ?: 0.0
        set(value) {
            _paint.saturation = value
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
    private var _layout: MTRasterLayout = MTRasterLayout()

    @Suppress("PropertyName")
    @SerialName("paint")
    private var _paint: MTRasterPaint = MTRasterPaint()

    constructor(
        identifier: String,
        sourceIdentifier: String,
    ) {
        this.identifier = identifier
        this.sourceIdentifier = sourceIdentifier

        this._layout = MTRasterLayout(visibility = visibility)
        this._paint =
            MTRasterPaint(
                brightnessMax = brightnessMax,
                brightnessMin = brightnessMin,
                contrast = contrast,
                fadeDuration = fadeDuration,
                hueRotate = hueRotate,
                opacity = opacity,
                resampling = resampling,
                saturation = saturation,
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

        this._layout = MTRasterLayout(visibility = visibility)
        this._paint =
            MTRasterPaint(
                brightnessMax = brightnessMax,
                brightnessMin = brightnessMin,
                contrast = contrast,
                fadeDuration = fadeDuration,
                hueRotate = hueRotate,
                opacity = opacity,
                resampling = resampling,
                saturation = saturation,
            )
    }

    constructor(
        identifier: String,
        type: MTLayerType,
        sourceIdentifier: String,
        maxZoom: Double?,
        minZoom: Double?,
        sourceLayer: String?,
        brightnessMax: Double?,
        brightnessMin: Double?,
        contrast: Double?,
        fadeDuration: Double?,
        hueRotate: Double?,
        opacity: Double?,
        resampling: MTRasterResampling?,
        saturation: Double?,
        visibility: MTLayerVisibility,
    ) {
        this.identifier = identifier
        this.type = type
        this.sourceIdentifier = sourceIdentifier
        this.maxZoom = maxZoom
        this.minZoom = minZoom
        this.sourceLayer = sourceLayer
        this.brightnessMax = brightnessMax
        this.brightnessMin = brightnessMin
        this.contrast = contrast
        this.fadeDuration = fadeDuration
        this.hueRotate = hueRotate
        this.opacity = opacity
        this.resampling = resampling
        this.saturation = saturation
        this.visibility = visibility

        this._layout =
            MTRasterLayout(
                visibility = visibility,
            )
        this._paint =
            MTRasterPaint(
                brightnessMax = brightnessMax,
                brightnessMin = brightnessMin,
                contrast = contrast,
                fadeDuration = fadeDuration,
                hueRotate = hueRotate,
                opacity = opacity,
                resampling = resampling,
                saturation = saturation,
            )
    }
}

@Serializable
internal data class MTRasterLayout(
    var visibility: MTLayerVisibility = MTLayerVisibility.VISIBLE,
)

@Serializable
internal data class MTRasterPaint(
    @SerialName("raster-brightness-max")
    var brightnessMax: Double? = 1.0,
    @SerialName("raster-brightness-min")
    var brightnessMin: Double? = 0.0,
    @SerialName("raster-contrast")
    var contrast: Double? = 0.0,
    @SerialName("raster-fade-duration")
    var fadeDuration: Double? = 300.0,
    @SerialName("raster-hue-rotate")
    var hueRotate: Double? = 0.0,
    @SerialName("raster-opacity")
    var opacity: Double? = 1.0,
    @SerialName("raster-resampling")
    var resampling: MTRasterResampling? = MTRasterResampling.LINEAR,
    @SerialName("raster-saturation")
    var saturation: Double? = 0.0,
)

/**
 * The resampling/interpolation method to use for overscaling, also known as texture magnification filter.
 */
@Serializable
enum class MTRasterResampling {
    @SerialName("linear")
    LINEAR,

    @SerialName("nearest")
    NEAREST,
}
