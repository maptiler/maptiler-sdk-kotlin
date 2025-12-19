/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.annotations

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType
import com.maptiler.maptilersdk.commands.annotations.GetTextPopupLngLat
import com.maptiler.maptilersdk.commands.annotations.IsTextPopupOpen
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapViewController
import java.util.UUID

class MTPopup(
    override val identifier: String = "mark${UUID.randomUUID().toString().replace("-", "")}",
    private var _coordinates: LngLat,
) : MTAnnotation {
    /**
     * Position of the popup on the map.
     */
    override val coordinates: LngLat
        get() = _coordinates

    /**
     * Text content of the popup.
     */
    var text: String = ""

    /**
     * The pixel distance from the popup's coordinates.
     */
    var offset: Double? = 0.0

    /**
     * CSS max width of the popup container (for example, "240px").
     */
    var maxWidth: String? = null

    /**
     * Boolean indicating whether the popup is currently displayed on the map.
     */
    var isOpen: Boolean = false
        private set

    private var boundBridge: MTBridge? = null

    constructor(
        coordinates: LngLat,
        text: String,
    ) : this(identifier = "mark${UUID.randomUUID().toString().replace("-", "")}", _coordinates = coordinates) {
        this.text = text
    }

    constructor(
        coordinates: LngLat,
        text: String,
        offset: Double,
    ) : this(identifier = "mark${UUID.randomUUID().toString().replace("-", "")}", _coordinates = coordinates) {
        this.text = text
        this.offset = offset
    }

    constructor(
        coordinates: LngLat,
        text: String,
        offset: Double,
        maxWidth: String?,
    ) : this(identifier = "mark${UUID.randomUUID().toString().replace("-", "")}", _coordinates = coordinates) {
        this.text = text
        this.offset = offset
        this.maxWidth = maxWidth
    }

    /**
     * Sets coordinates for the popup.
     *
     * @param coordinates Position of the popup.
     */
    override fun setCoordinates(
        coordinates: LngLat,
        mapViewController: MTMapViewController,
    ) {
        this._coordinates = coordinates

        mapViewController.setCoordinatesToTextPopup(this)
    }

    /**
     * Sets the max width for the popup and updates it on the map.
     *
     * @param maxWidth CSS width value (e.g. "240px").
     */
    fun setMaxWidth(
        maxWidth: String,
        mapViewController: MTMapViewController,
    ) {
        this.maxWidth = maxWidth

        mapViewController.setMaxWidthToTextPopup(this)
    }

    /**
     * Returns the current coordinates of the popup.
     */
    suspend fun getLngLat(): LngLat {
        val bridge = boundBridge ?: return coordinates

        val returnTypeValue = bridge.execute(GetTextPopupLngLat(this))
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
     * Refreshes the open state of the popup from the map.
     */
    suspend fun refreshIsOpen(): Boolean {
        val bridge = boundBridge ?: return isOpen

        val returnTypeValue = bridge.execute(IsTextPopupOpen(this))
        val parsed = parseBooleanValue(returnTypeValue, isOpen)

        isOpen = parsed
        return parsed
    }

    internal fun bindBridge(bridge: MTBridge) {
        boundBridge = bridge
    }

    internal fun setOpenState(isOpen: Boolean) {
        this.isOpen = isOpen
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

typealias MTTextPopup = MTPopup
