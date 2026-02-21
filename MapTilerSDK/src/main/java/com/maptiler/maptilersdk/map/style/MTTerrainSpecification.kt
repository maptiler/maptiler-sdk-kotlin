/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style

import kotlinx.serialization.Serializable

/**
 * Specification for the 3D terrain representation.
 */
@Serializable
data class MTTerrainSpecification(
    /**
     * ID of the terrain source.
     */
    val source: String,
    /**
     * Exaggeration factor of the terrain.
     */
    val exaggeration: Double? = null,
)
