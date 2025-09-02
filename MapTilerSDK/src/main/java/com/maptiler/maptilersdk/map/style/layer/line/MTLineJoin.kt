/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.line

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The display of lines when joining.
 */
@Serializable
enum class MTLineJoin {
    /**
     * A join with a squared-off end which is drawn beyond the endpoint
     * of the line at a distance of one-half of the line’s width.
     */
    @SerialName("bevel")
    BEVEL,

    /**
     * A join with a rounded end which is drawn beyond the endpoint of the line
     * at a radius of one-half of the line’s width and centered on the endpoint of the line.
     *
     */
    @SerialName("round")
    ROUND,

    /**
     * A join with a sharp, angled corner which is drawn with the outer
     * sides beyond the endpoint of the path until they meet.
     */
    @SerialName("miter")
    MITER,
}
