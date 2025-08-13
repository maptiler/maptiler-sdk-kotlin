/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.types

import com.maptiler.maptilersdk.map.LngLat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Object sent together with MTEvent.
 */
@Serializable
data class MTData(
    /**
     * Unique id.
     */
    val id: String? = null,
    /**
     * Coordinate of the event tap.
     */
    @SerialName("lngLat")
    val coordinate: LngLat? = null,
    /**
     * Point of the event tap.
     */
    val point: MTPoint? = null,
    /**
     * Type of the event.
     */
    val dataType: String? = null,
    /**
     * Boolean indicating if source is fully  loaded.
     */
    val isSourceLoaded: Boolean? = null,
    /**
     * Source data.
     */
    val source: MTSourceData? = null,
    /**
     * Type of the source data.
     */
    val sourceDataType: String? = null,
)
