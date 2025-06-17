/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MTMapCorner {
    /**
     * Top left corner of the map.
     */
    @SerialName("top-left")
    TOP_LEFT,

    /**
     * Top right corner of the map.
     */
    @SerialName("top-right")
    TOP_RIGHT,

    /**
     * Bottom left corner of the map.
     */
    @SerialName("bottom-left")
    BOTTOM_LEFT,

    /**
     * Bottom right corner of the map.
     */
    @SerialName("bottom-right")
    BOTTOM_RIGHT,
}
