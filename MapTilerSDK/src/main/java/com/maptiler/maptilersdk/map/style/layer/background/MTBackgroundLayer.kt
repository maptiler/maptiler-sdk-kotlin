/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.background

import android.graphics.Color
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import com.maptiler.maptilersdk.map.style.layer.MTLayerVisibility
import com.maptiler.maptilersdk.map.style.layer.fill.ColorAsHexSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The background style layer covers the entire map. Use it to configure a color or pattern
 * rendered beneath all other map content. If the background layer is transparent or omitted,
 * any area not covered by another layer is transparent.
 */
@Serializable
class MTBackgroundLayer : MTLayer {
    /**
     * Unique layer identifier.
     */
    @SerialName("id")
    override var identifier: String

    /**
     * Type of the layer. Always [MTLayerType.BACKGROUND].
     */
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override var type: MTLayerType = MTLayerType.BACKGROUND
        private set

    /**
     * Background layers do not have a source. Kept only to satisfy [MTLayer] contract
     * and excluded from serialization.
     */
    @Transient
    override var sourceIdentifier: String = ""

    /**
     * Max zoom of the layer.
     */
    @SerialName("maxzoom")
    override var maxZoom: Double? = null

    /**
     * Min zoom of the layer.
     */
    @SerialName("minzoom")
    override var minZoom: Double? = null

    /**
     * Source layer is not applicable for background layers. Excluded from serialization.
     */
    @Transient
    override var sourceLayer: String? = null

    /**
     * The color with which the background will be drawn.
     * Defaults to black. Disabled by [pattern].
     */
    @Serializable(with = ColorAsHexSerializer::class)
    var color: Int?
        get() = _paint.color ?: Color.BLACK
        set(value) {
            _paint.color = value
        }

    /**
     * The opacity at which the background will be drawn.
     * Optional number between 0 and 1 inclusive. Defaults to 1.
     */
    var opacity: Double?
        get() = _paint.opacity ?: 1.0
        set(value) {
            _paint.opacity = value
        }

    /**
     * Name of image in sprite to use for drawing an image background.
     * For seamless patterns, image width and height must be a power of two.
     */
    var pattern: String?
        get() = _paint.pattern
        set(value) {
            _paint.pattern = value
        }

    /**
     * Whether this layer is displayed.
     */
    var visibility: MTLayerVisibility
        get() = MTLayerVisibility.from(_layout.visibility) ?: MTLayerVisibility.VISIBLE
        set(value) {
            _layout.visibility = value
        }

    @Suppress("PropertyName")
    @SerialName("layout")
    private var _layout: MTBackgroundLayout = MTBackgroundLayout()

    @Suppress("PropertyName")
    @SerialName("paint")
    private var _paint: MTBackgroundPaint = MTBackgroundPaint()

    constructor(identifier: String) {
        this.identifier = identifier
        this._layout = MTBackgroundLayout(visibility = visibility)
        this._paint = MTBackgroundPaint(color = color, opacity = opacity, pattern = pattern)
    }

    constructor(
        identifier: String,
        type: MTLayerType,
        maxZoom: Double?,
        minZoom: Double?,
        color: Int?,
        opacity: Double?,
        pattern: String?,
        visibility: MTLayerVisibility,
    ) {
        this.identifier = identifier
        this.type = type
        this.maxZoom = maxZoom
        this.minZoom = minZoom
        this.color = color
        this.opacity = opacity
        this.pattern = pattern
        this.visibility = visibility
        this._layout = MTBackgroundLayout(visibility = visibility)
        this._paint = MTBackgroundPaint(color = color, opacity = opacity, pattern = pattern)
    }
}

@Serializable
internal data class MTBackgroundLayout(
    var visibility: MTLayerVisibility = MTLayerVisibility.VISIBLE,
)

@Serializable
internal data class MTBackgroundPaint(
    @Serializable(with = ColorAsHexSerializer::class)
    @SerialName("background-color")
    var color: Int? = Color.BLACK,
    @SerialName("background-opacity")
    var opacity: Double? = 1.0,
    @SerialName("background-pattern")
    var pattern: String? = null,
)
