/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.types

import kotlinx.serialization.Serializable

/**
 * Identifier for a feature in a source.
 *
 * @param source The ID of the source.
 * @param sourceLayer The ID of the layer within the source (for vector sources).
 * @param id The ID of the feature.
 */
@Serializable
data class MTFeatureIdentifier(
    val source: String,
    val sourceLayer: String? = null,
    val id: String,
)
