/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MTProjectionType {
    /**
     * Mercator projection.
     */
    @SerialName("mercator")
    MERCATOR,

    /**
     * Globe projection.
     */
    @SerialName("globe")
    GLOBE,
}
