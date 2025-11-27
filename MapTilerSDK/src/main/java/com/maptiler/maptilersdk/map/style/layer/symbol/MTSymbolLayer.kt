/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.symbol

import android.graphics.Bitmap
import com.maptiler.maptilersdk.map.style.dsl.MTTextToken
import com.maptiler.maptilersdk.map.style.dsl.PropertyValue
import com.maptiler.maptilersdk.map.style.dsl.StyleValue
import com.maptiler.maptilersdk.map.style.dsl.StyleValueSerializer
import com.maptiler.maptilersdk.map.style.dsl.toJsonElement
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import com.maptiler.maptilersdk.map.style.layer.MTLayerVisibility
import com.maptiler.maptilersdk.map.types.MTPoint
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray

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

    @Suppress("PropertyName")
    @SerialName("filter")
    private var _filter: JsonElement? = null

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
                _layout = MTSymbolLayout(iconImage = iconName, visibility = visibility)
            }
        }

    /**
     * Label text to render for each feature, e.g. "{point_count_abbreviated}" for clusters.
     */
    var textField: String?
        get() = _layout?.textField
        set(value) {
            ensureLayout()
            _layout?.textField = value
        }

    /**
     * Label text size in pixels.
     */
    var textSize: Double?
        get() = _layout?.textSize
        set(value) {
            ensureLayout()
            _layout?.textSize = value
        }

    /**
     * Anchor position of the text relative to the symbol anchor.
     * For example, use [MTTextAnchor.TOP] with a positive Y [textOffset] to render text below the icon.
     */
    var textAnchor: MTTextAnchor?
        get() = _layout?.textAnchor?.let { MTTextAnchor.from(it) }
        set(value) {
            ensureLayout()
            _layout?.textAnchor = value?.value
        }

    /**
     * Amount to offset the text from its anchor in ems. X is right, Y is down.
     * Example: [0.0, 1.2] with [textAnchor] = [MTTextAnchor.TOP] places text under the icon.
     */
    var textOffset: MTPoint?
        get() = _layout?.textOffset?.let { if (it.size >= 2) MTPoint(it[0], it[1]) else null }
        set(value) {
            ensureLayout()
            _layout?.textOffset = value?.let { listOf(it.x, it.y) }
        }

    /**
     * If true, the icon will be visible even if it collides with other symbols.
     * Defaults to style spec default (false) when not set.
     */
    var iconAllowOverlap: Boolean?
        get() = _layout?.iconAllowOverlap
        set(value) {
            ensureLayout()
            _layout?.iconAllowOverlap = value
        }

    /**
     * If true, the text will be visible even if it collides with other symbols.
     * Defaults to style spec default (false) when not set.
     */
    var textAllowOverlap: Boolean?
        get() = _layout?.textAllowOverlap
        set(value) {
            ensureLayout()
            _layout?.textAllowOverlap = value
        }

    /**
     * If true, the icon will be visible and will not affect placement of other symbols.
     * Defaults to style spec default (false) when not set.
     */
    var iconIgnorePlacement: Boolean?
        get() = _layout?.iconIgnorePlacement
        set(value) {
            ensureLayout()
            _layout?.iconIgnorePlacement = value
        }

    /**
     * If true, the text will be visible and will not affect placement of other symbols.
     * Defaults to style spec default (false) when not set.
     */
    var textIgnorePlacement: Boolean?
        get() = _layout?.textIgnorePlacement
        set(value) {
            ensureLayout()
            _layout?.textIgnorePlacement = value
        }

    /**
     * Text fonts as style-spec family names.
     */
    var textFont: List<String>?
        get() = _layout?.textFont
        set(value) {
            ensureLayout()
            _layout?.textFont = value
        }

    /**
     * Text color (constant or expression), written under paint as text-color.
     */
    var textColor: StyleValue?
        get() = _paint?.textColor
        set(value) {
            if (value != null) {
                if (_paint == null) _paint = MTSymbolPaint()
                _paint?.textColor = value
            } else {
                _paint = null
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

    @Suppress("PropertyName")
    @SerialName("paint")
    private var _paint: MTSymbolPaint? = null

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
        this._layout = MTSymbolLayout(iconImage = iconName, visibility = visibility)
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
        this._layout = MTSymbolLayout(iconImage = iconName, visibility = visibility)
    }

    private fun ensureLayout() {
        if (_layout == null) {
            _layout = MTSymbolLayout(iconImage = iconName, visibility = visibility)
        }
    }

    /**
     * Sets filter to ["has", field].
     */
    fun setFilterHas(field: String) {
        _filter =
            buildJsonArray {
                add(JsonPrimitive("has"))
                add(JsonPrimitive(field))
            }
    }

    /**
     * Sets filter to ["!", ["has", field]].
     */
    fun setFilterNotHas(field: String) {
        _filter =
            buildJsonArray {
                add(JsonPrimitive("!"))
                add(
                    buildJsonArray {
                        add(JsonPrimitive("has"))
                        add(JsonPrimitive(field))
                    },
                )
            }
    }

    // Inline filter DSL
    fun withFilter(expr: PropertyValue) {
        _filter = expr.toJsonElement()
    }
}

@Serializable
internal data class MTSymbolLayout(
    @SerialName("icon-image")
    var iconImage: String = "",
    @SerialName("text-field")
    var textField: String? = null,
    @SerialName("text-size")
    var textSize: Double? = null,
    @SerialName("text-anchor")
    var textAnchor: String? = null,
    @SerialName("text-offset")
    var textOffset: List<Double>? = null,
    @SerialName("text-font")
    var textFont: List<String>? = null,
    @SerialName("icon-allow-overlap")
    var iconAllowOverlap: Boolean? = null,
    @SerialName("text-allow-overlap")
    var textAllowOverlap: Boolean? = null,
    @SerialName("icon-ignore-placement")
    var iconIgnorePlacement: Boolean? = null,
    @SerialName("text-ignore-placement")
    var textIgnorePlacement: Boolean? = null,
    var visibility: MTLayerVisibility = MTLayerVisibility.VISIBLE,
)

@Serializable
internal data class MTSymbolPaint(
    @SerialName("text-color")
    @Serializable(with = StyleValueSerializer::class)
    var textColor: StyleValue? = null,
)

// DSL helpers

fun MTSymbolLayer.textField(value: String) = apply { this.textField = value }

fun MTSymbolLayer.textField(token: MTTextToken) = apply { this.textField = token.token }

fun MTSymbolLayer.textSize(value: Double) = apply { this.textSize = value }

fun MTSymbolLayer.textAllowOverlap(value: Boolean) = apply { this.textAllowOverlap = value }

fun MTSymbolLayer.textAnchor(value: MTTextAnchor) = apply { this.textAnchor = value }

fun MTSymbolLayer.textFont(value: List<String>) = apply { this.textFont = value }

fun MTSymbolLayer.textColorConst(color: Int) = apply { this.textColor = StyleValue.Color(color) }

fun MTSymbolLayer.textColorExpr(expr: PropertyValue) = apply { this.textColor = StyleValue.Expression(expr) }
