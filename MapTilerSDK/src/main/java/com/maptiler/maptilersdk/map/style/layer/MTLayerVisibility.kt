/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer

import kotlinx.serialization.Serializable

@Serializable
enum class MTLayerVisibility {
    /**
     * Layer is shown.
     */
    VISIBLE,

    /**
     * Layer is not shown.
     */
    NONE,
}
