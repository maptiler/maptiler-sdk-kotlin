/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.line

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The display of line endings.
 */
@Serializable
enum class MTLineCap {
    /**
     * A cap with a squared-off end which is drawn to the exact endpoint of the line.
     */
    @SerialName("butt")
    BUTT,

    /**
     * A cap with a rounded end which is drawn beyond the endpoint of the line
     * at a radius of one-half of the line’s width and centered on the endpoint of the line.
     */
    @SerialName("round")
    ROUND,

    /**
     * A cap with a squared-off end which is drawn beyond the endpoint of the line
     * at a distance of one-half of the line’s width.
     */
    @SerialName("square")
    SQUARE,
}
