/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.annotations

import android.graphics.Bitmap
import android.graphics.Color
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType
import com.maptiler.maptilersdk.commands.annotations.GetMarkerLngLat
import com.maptiler.maptilersdk.commands.annotations.GetMarkerOffset
import com.maptiler.maptilersdk.commands.annotations.GetMarkerPitchAlignment
import com.maptiler.maptilersdk.commands.annotations.GetMarkerRotation
import com.maptiler.maptilersdk.commands.annotations.GetMarkerRotationAlignment
import com.maptiler.maptilersdk.commands.annotations.IsMarkerDraggable
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapViewController
import java.util.UUID

/**
 * Annotation element that can be added to the map.
 */
class MTMarker(
    override val identifier: String = "mark${UUID.randomUUID().toString().replace("-", "")}",
    private var _coordinates: LngLat,
) : MTAnnotation {
    /**
     * Position of the marker on the map.
     */
    override val coordinates: LngLat
        get() = _coordinates

    /**
     * Color of the marker.
     */
    var color: Int? = Color.BLUE

    /**
     * Opacity of the marker.
     */
    var opacity: Double = 1.0

    /**
     * Opacity of the marker when it is covered by another 3D object.
     */
    var opacityWhenCovered: Double = 1.0

    /**
     * Boolean indicating whether marker is draggable.
     */
    var draggable: Boolean? = false

    /**
     * Optional attached popup.
     */
    var popup: MTTextPopup? = null

    /**
     * Custom icon to use for marker.
     */
    var icon: Bitmap? = null

    /**
     * Anchor used to align the marker relative to its coordinates.
     */
    var anchor: MTAnchor = MTAnchor.CENTER

    /**
     * Alignment for marker pitch relative to the map or viewport.
     */
    var pitchAlignment: MTPitchAlignment = MTPitchAlignment.AUTO

    /**
     * Rotation of the marker in degrees.
     */
    var rotation: Double = 0.0

    /**
     * Scale of the marker icon.
     */
    var scale: Double = 1.0

    /**
     * Alignment for marker rotation relative to the map or viewport.
     */
    var rotationAlignment: MTRotationAlignment = MTRotationAlignment.AUTO

    /**
     * Pixel offset to apply when positioning the marker relative to its anchor.
     */
    var offset: Double = 0.0

    /**
     * Enables subpixel positioning for smoother rendering.
     */
    var subpixelPositioning: Boolean = true

    private var tapThreshold: Double = 30.0

    private var boundBridge: MTBridge? = null

    constructor(
        coordinates: LngLat,
        color: Int? = Color.BLUE,
        icon: Bitmap? = null,
        draggable: Boolean? = false,
    ) : this(identifier = "mark${UUID.randomUUID().toString().replace("-", "")}", _coordinates = coordinates) {
        this.color = color
        this.icon = icon
        this.draggable = draggable
    }

    constructor(
        coordinates: LngLat,
    ) : this(identifier = "mark${UUID.randomUUID().toString().replace("-", "")}", _coordinates = coordinates) {
        // Default
    }

    constructor(
        coordinates: LngLat,
        icon: Bitmap,
    ) : this(identifier = "mark${UUID.randomUUID().toString().replace("-", "")}", _coordinates = coordinates) {
        this.icon = icon
    }

    constructor(
        coordinates: LngLat,
        icon: Bitmap,
        popup: MTTextPopup,
    ) : this(identifier = "mark${UUID.randomUUID().toString().replace("-", "")}", _coordinates = coordinates) {
        this.icon = icon
        this.popup = popup
    }

    constructor(
        coordinates: LngLat,
        popup: MTTextPopup,
    ) : this(identifier = "mark${UUID.randomUUID().toString().replace("-", "")}", _coordinates = coordinates) {
        this.popup = popup
    }

    /**
     * Sets coordinates for the marker.
     *
     * @param coordinates Position of the marker.
     */
    override fun setCoordinates(
        coordinates: LngLat,
        mapViewController: MTMapViewController,
    ) {
        this._coordinates = coordinates

        mapViewController.setCoordinatesToMarker(this)
    }

    /**
     * Sets draggable flag for the marker.
     *
     * @param draggable Boolean value of draggable property.
     */
    fun setDraggable(
        draggable: Boolean,
        mapViewController: MTMapViewController,
    ) {
        this.draggable = draggable

        mapViewController.setDraggableToMarker(this, draggable)
    }

    /**
     * Sets pixel offset of the marker.
     *
     * @param offset Offset in pixels.
     */
    fun setOffset(
        offset: Double,
        mapViewController: MTMapViewController,
    ) {
        this.offset = offset

        mapViewController.setOffsetToMarker(this, offset)
    }

    /**
     * Sets rotation of the marker.
     *
     * @param rotation Rotation in degrees.
     */
    fun setRotation(
        rotation: Double,
        mapViewController: MTMapViewController,
    ) {
        this.rotation = rotation

        mapViewController.setRotationToMarker(this, rotation)
    }

    /**
     * Sets rotation alignment of the marker.
     *
     * @param rotationAlignment Rotation alignment value.
     */
    fun setRotationAlignment(
        rotationAlignment: MTRotationAlignment,
        mapViewController: MTMapViewController,
    ) {
        this.rotationAlignment = rotationAlignment

        mapViewController.setRotationAlignmentToMarker(this, rotationAlignment)
    }

    /**
     * Toggles popup for marker.
     */
    fun togglePopup(mapViewController: MTMapViewController) {
        mapViewController.toggleMarkerPopup(this)
    }

    internal fun bindBridge(bridge: MTBridge) {
        boundBridge = bridge
    }

    /**
     * Returns the current coordinates of the marker.
     */
    suspend fun getLngLat(): LngLat {
        val bridge = boundBridge ?: return coordinates

        val returnTypeValue = bridge.execute(GetMarkerLngLat(this))
        val parsed =
            when (returnTypeValue) {
                is MTBridgeReturnType.StringValue ->
                    runCatching { JsonConfig.json.decodeFromString<LngLat>(returnTypeValue.value) }.getOrNull()
                is MTBridgeReturnType.StringDoubleDict ->
                    returnTypeValue.value["lng"]?.let { lng ->
                        returnTypeValue.value["lat"]?.let { lat -> LngLat(lng, lat) }
                    }
                else -> null
            } ?: coordinates

        _coordinates = parsed
        return parsed
    }

    /**
     * Returns the pitch alignment of the marker.
     */
    suspend fun getPitchAlignment(): MTPitchAlignment {
        val bridge = boundBridge ?: return pitchAlignment

        val returnTypeValue = bridge.execute(GetMarkerPitchAlignment(this))
        val parsed = parsePitchAlignment(returnTypeValue) ?: pitchAlignment

        pitchAlignment = parsed
        return parsed
    }

    /**
     * Returns the rotation of the marker in degrees.
     */
    suspend fun getRotation(): Double {
        val bridge = boundBridge ?: return rotation

        val returnTypeValue = bridge.execute(GetMarkerRotation(this))
        val parsed = parseDoubleValue(returnTypeValue, rotation)

        rotation = parsed
        return parsed
    }

    /**
     * Returns the rotation alignment of the marker.
     */
    suspend fun getRotationAlignment(): MTRotationAlignment {
        val bridge = boundBridge ?: return rotationAlignment

        val returnTypeValue = bridge.execute(GetMarkerRotationAlignment(this))
        val parsed = parseRotationAlignment(returnTypeValue) ?: rotationAlignment

        rotationAlignment = parsed
        return parsed
    }

    /**
     * Returns the offset applied to the marker.
     */
    suspend fun getOffset(): Double {
        val bridge = boundBridge ?: return offset

        val returnTypeValue = bridge.execute(GetMarkerOffset(this))
        val parsed = parseDoubleValue(returnTypeValue, offset)

        offset = parsed
        return parsed
    }

    /**
     * Returns boolean indicating whether the marker can be dragged.
     */
    suspend fun isDraggable(): Boolean {
        val bridge = boundBridge ?: return draggable ?: false

        val returnTypeValue = bridge.execute(IsMarkerDraggable(this))
        val parsed = parseBooleanValue(returnTypeValue, draggable ?: false)

        draggable = parsed
        return parsed
    }

    private fun parsePitchAlignment(returnTypeValue: MTBridgeReturnType?): MTPitchAlignment? {
        if (returnTypeValue !is MTBridgeReturnType.StringValue) {
            return null
        }

        val normalized = returnTypeValue.value.trim('"').trim().lowercase()

        return MTPitchAlignment.values().firstOrNull { it.value == normalized }
    }

    private fun parseRotationAlignment(returnTypeValue: MTBridgeReturnType?): MTRotationAlignment? {
        if (returnTypeValue !is MTBridgeReturnType.StringValue) {
            return null
        }

        val normalized = returnTypeValue.value.trim('"').trim().lowercase()

        return MTRotationAlignment.values().firstOrNull { it.value == normalized }
    }

    private fun parseDoubleValue(
        returnTypeValue: MTBridgeReturnType?,
        defaultValue: Double,
    ): Double =
        when (returnTypeValue) {
            is MTBridgeReturnType.DoubleValue -> returnTypeValue.value
            is MTBridgeReturnType.StringValue -> returnTypeValue.value.trim('"').toDoubleOrNull() ?: defaultValue
            else -> defaultValue
        }

    private fun parseBooleanValue(
        returnTypeValue: MTBridgeReturnType?,
        defaultValue: Boolean,
    ): Boolean =
        when (returnTypeValue) {
            is MTBridgeReturnType.BoolValue -> returnTypeValue.value
            is MTBridgeReturnType.DoubleValue -> returnTypeValue.value != 0.0
            is MTBridgeReturnType.StringValue -> {
                val normalized = returnTypeValue.value.trim('"').trim().lowercase()
                when (normalized) {
                    "true" -> true
                    "false" -> false
                    else -> normalized.toDoubleOrNull()?.let { it != 0.0 } ?: defaultValue
                }
            }
            else -> defaultValue
        }
}
