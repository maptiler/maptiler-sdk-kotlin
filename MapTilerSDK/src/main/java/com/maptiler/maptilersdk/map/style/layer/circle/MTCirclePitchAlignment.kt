/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.circle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Controls the alignment of circles when the map pitch is applied.
 */
@Serializable
enum class MTCirclePitchAlignment {
    /** Circles are aligned to the viewport. */
    @SerialName("viewport")
    VIEWPORT,

    /** Circles are aligned to the map. */
    @SerialName("map")
    MAP,
}
