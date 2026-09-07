/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Unit of the scale control.
 */
@Serializable
enum class MTScaleUnit {
    /**
     * Imperial unit.
     */
    @SerialName("imperial")
    IMPERIAL,

    /**
     * Metric unit.
     */
    @SerialName("metric")
    METRIC,

    /**
     * Nautical unit.
     */
    @SerialName("nautical")
    NAUTICAL,
}
