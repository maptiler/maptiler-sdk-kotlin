/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.line

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Controls the frame of reference for line translate.
 */
@Serializable
enum class MTLineTranslateAnchor {
    /**
     * The line is translated relative to the map.
     */
    @SerialName("map")
    MAP,

    /**
     * The line is translated relative to the viewport.
     */
    @SerialName("viewport")
    VIEWPORT,
}
