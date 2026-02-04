/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer.hillshade

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Direction frame for hillshade illumination when the map is rotated.
 *
 * - [MAP]: Illumination relative to north.
 * - [VIEWPORT]: Illumination relative to the top of the viewport.
 */
@Serializable
enum class MTHillshadeIlluminationAnchor {
    @SerialName("map")
    MAP,

    @SerialName("viewport")
    VIEWPORT,
}
