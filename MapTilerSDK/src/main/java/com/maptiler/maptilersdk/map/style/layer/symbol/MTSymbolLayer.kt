/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.symbol

import android.graphics.Bitmap
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import com.maptiler.maptilersdk.map.style.layer.MTLayerVisibility
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The symbol style that layer renders icon and text labels at points or along lines on a map.
 */
@Serializable
class MTSymbolLayer : MTLayer {
    /**
     * Unique layer identifier.
     */
    @SerialName("id")
    override var identifier: String

    /**
     * Type of the layer.
     */
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override var type: MTLayerType = MTLayerType.SYMBOL
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
     * Icon to use for the layer.
     */
    @Transient
    var icon: Bitmap? = null
        set(value) {
            field = value
            // If an icon is assigned post-construction and layout wasn't initialized,
            // ensure the layout exists and references this layer's icon image name.
            if (value != null && _layout == null) {
                _layout = MTSymbolLayout(iconName, visibility)
            }
        }

    /**
     * Enum controlling whether this layer is displayed.
     */
    var visibility: MTLayerVisibility
        get() = _layout?.let { MTLayerVisibility.from(it.visibility) } ?: _visibility
        set(value) {
            _layout?.let { it.visibility = value } ?: run { _visibility = value }
        }

    private var iconName: String
        get() = "icon$identifier"
        set(value) {
            _layout?.iconImage = value
        }

    @Suppress("PropertyName")
    @SerialName("layout")
    private var _layout: MTSymbolLayout? = null

    @Transient
    private var _visibility: MTLayerVisibility = MTLayerVisibility.VISIBLE

    constructor(
        identifier: String,
        sourceIdentifier: String,
    ) {
        this.identifier = identifier
        this.sourceIdentifier = sourceIdentifier
        this._layout = null
    }

    constructor(
        identifier: String,
        sourceIdentifier: String,
        icon: Bitmap,
    ) {
        this.identifier = identifier
        this.sourceIdentifier = sourceIdentifier
        this.icon = icon
        this._layout = MTSymbolLayout(iconName, visibility)
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
    }

    constructor(
        identifier: String,
        sourceIdentifier: String,
        maxZoom: Double,
        minZoom: Double,
        sourceLayer: String,
        icon: Bitmap,
        visibility: MTLayerVisibility,
    ) {
        this.identifier = identifier
        this.sourceIdentifier = sourceIdentifier
        this.maxZoom = maxZoom
        this.minZoom = minZoom
        this.sourceLayer = sourceLayer
        this.icon = icon
        this._visibility = visibility
        this._layout = MTSymbolLayout(iconName, visibility)
    }
}

@Serializable
internal data class MTSymbolLayout(
    @SerialName("icon-image")
    var iconImage: String = "",
    var visibility: MTLayerVisibility = MTLayerVisibility.VISIBLE,
)
