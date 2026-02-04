/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.hillshade

import android.graphics.Color
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import com.maptiler.maptilersdk.map.style.layer.MTLayerVisibility
import com.maptiler.maptilersdk.map.style.layer.fill.ColorAsHexSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A hillshade style layer renders digital elevation model (DEM) data on the client-side.
 *
 * Supports Terrain RGB and Mapzen Terrarium encodings via a `raster-dem` source.
 */
@Serializable
class MTHillshadeLayer : MTLayer {
    /** Unique layer identifier. */
    @SerialName("id")
    override var identifier: String

    /** Type of the layer. */
    override var type: MTLayerType = MTLayerType.HILLSHADE
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

    /** Vector tile source layer name (not used for raster-dem). */
    @SerialName("source-layer")
    override var sourceLayer: String? = null

    // Paint

    /** The shading color used to accentuate rugged terrain like sharp cliffs and gorges. */
    @Serializable(with = ColorAsHexSerializer::class)
    var accentColor: Int?
        get() = _paint.accentColor ?: Color.BLACK
        set(value) {
            _paint.accentColor = value
        }

    /** Intensity of the hillshade in [0, 1]. */
    var exaggeration: Double?
        get() = _paint.exaggeration ?: 0.5
        set(value) {
            _paint.exaggeration = value
        }

    /** The shading color of areas that face towards the light source. */
    @Serializable(with = ColorAsHexSerializer::class)
    var highlightColor: Int?
        get() = _paint.highlightColor ?: Color.WHITE
        set(value) {
            _paint.highlightColor = value
        }

    /** Direction frame for illumination when map is rotated. */
    var illuminationAnchor: MTHillshadeIlluminationAnchor?
        get() = _paint.illuminationAnchor ?: MTHillshadeIlluminationAnchor.VIEWPORT
        set(value) {
            _paint.illuminationAnchor = value
        }

    /** The direction of the light source (0..359). */
    var illuminationDirection: Double?
        get() = _paint.illuminationDirection ?: 335.0
        set(value) {
            _paint.illuminationDirection = value
        }

    /** The shading color of areas that face away from the light source. */
    @Serializable(with = ColorAsHexSerializer::class)
    var shadowColor: Int?
        get() = _paint.shadowColor ?: Color.BLACK
        set(value) {
            _paint.shadowColor = value
        }

    // Layout

    /** Controls whether this layer is displayed. */
    var visibility: MTLayerVisibility
        get() = MTLayerVisibility.from(_layout.visibility) ?: MTLayerVisibility.VISIBLE
        set(value) {
            _layout.visibility = value
        }

    @Suppress("PropertyName")
    @SerialName("layout")
    private var _layout: MTHillshadeLayout = MTHillshadeLayout()

    @Suppress("PropertyName")
    @SerialName("paint")
    private var _paint: MTHillshadePaint = MTHillshadePaint()

    constructor(
        identifier: String,
        sourceIdentifier: String,
    ) {
        this.identifier = identifier
        this.sourceIdentifier = sourceIdentifier

        this._layout = MTHillshadeLayout(visibility = visibility)
        this._paint =
            MTHillshadePaint(
                accentColor = accentColor,
                exaggeration = exaggeration,
                highlightColor = highlightColor,
                illuminationAnchor = illuminationAnchor,
                illuminationDirection = illuminationDirection,
                shadowColor = shadowColor,
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

        this._layout = MTHillshadeLayout(visibility = visibility)
        this._paint =
            MTHillshadePaint(
                accentColor = accentColor,
                exaggeration = exaggeration,
                highlightColor = highlightColor,
                illuminationAnchor = illuminationAnchor,
                illuminationDirection = illuminationDirection,
                shadowColor = shadowColor,
            )
    }

    constructor(
        identifier: String,
        type: MTLayerType,
        sourceIdentifier: String,
        maxZoom: Double?,
        minZoom: Double?,
        sourceLayer: String?,
        accentColor: Int?,
        exaggeration: Double?,
        highlightColor: Int?,
        illuminationAnchor: MTHillshadeIlluminationAnchor?,
        illuminationDirection: Double?,
        shadowColor: Int?,
        visibility: MTLayerVisibility,
    ) {
        this.identifier = identifier
        this.type = type
        this.sourceIdentifier = sourceIdentifier
        this.maxZoom = maxZoom
        this.minZoom = minZoom
        this.sourceLayer = sourceLayer
        this.accentColor = accentColor
        this.exaggeration = exaggeration
        this.highlightColor = highlightColor
        this.illuminationAnchor = illuminationAnchor
        this.illuminationDirection = illuminationDirection
        this.shadowColor = shadowColor
        this.visibility = visibility

        this._layout =
            MTHillshadeLayout(
                visibility = visibility,
            )
        this._paint =
            MTHillshadePaint(
                accentColor = accentColor,
                exaggeration = exaggeration,
                highlightColor = highlightColor,
                illuminationAnchor = illuminationAnchor,
                illuminationDirection = illuminationDirection,
                shadowColor = shadowColor,
            )
    }
}

@Serializable
internal data class MTHillshadeLayout(
    var visibility: MTLayerVisibility = MTLayerVisibility.VISIBLE,
)

@Serializable
internal data class MTHillshadePaint(
    @Serializable(with = ColorAsHexSerializer::class)
    @SerialName("hillshade-accent-color")
    var accentColor: Int? = Color.BLACK,
    @SerialName("hillshade-exaggeration")
    var exaggeration: Double? = 0.5,
    @Serializable(with = ColorAsHexSerializer::class)
    @SerialName("hillshade-highlight-color")
    var highlightColor: Int? = Color.WHITE,
    @SerialName("hillshade-illumination-anchor")
    var illuminationAnchor: MTHillshadeIlluminationAnchor? = MTHillshadeIlluminationAnchor.VIEWPORT,
    @SerialName("hillshade-illumination-direction")
    var illuminationDirection: Double? = 335.0,
    @Serializable(with = ColorAsHexSerializer::class)
    @SerialName("hillshade-shadow-color")
    var shadowColor: Int? = Color.BLACK,
)
