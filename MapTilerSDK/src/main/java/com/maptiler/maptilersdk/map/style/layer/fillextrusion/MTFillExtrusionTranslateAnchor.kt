/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.fillextrusion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Enum controlling the frame of reference for fill-extrusion translate.
 */
@Serializable
enum class MTFillExtrusionTranslateAnchor {
    /** The fill extrusion is translated relative to the map. */
    @SerialName("map")
    MAP,

    /** The fill extrusion is translated relative to the viewport. */
    @SerialName("viewport")
    VIEWPORT,
}
