/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.types

import com.maptiler.maptilersdk.map.LngLat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MTData(
    val id: String? = null,
    @SerialName("lngLat")
    val coordinate: LngLat? = null,
    val point: MTPoint? = null,
    val dataType: String? = null,
    val isSourceLoaded: Boolean? = null,
    val source: MTSourceData? = null,
    val sourceDataType: String? = null,
)
