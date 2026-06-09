/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import kotlinx.serialization.Serializable

/**
 * Represents a range of zoom levels for offline content.
 */
@Serializable
data class MTOfflineZoomRange(
    /**
     * The minimum zoom level.
     */
    val minZoom: Int,
    /**
     * The maximum zoom level.
     */
    val maxZoom: Int,
)
