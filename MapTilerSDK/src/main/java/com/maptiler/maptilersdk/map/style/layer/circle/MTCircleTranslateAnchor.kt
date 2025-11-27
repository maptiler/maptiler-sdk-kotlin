/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.circle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Controls the frame of reference for circle-translate.
 */
@Serializable
enum class MTCircleTranslateAnchor {
    /** The circle is translated relative to the map. */
    @SerialName("map")
    MAP,

    /** The circle is translated relative to the viewport. */
    @SerialName("viewport")
    VIEWPORT,
}
