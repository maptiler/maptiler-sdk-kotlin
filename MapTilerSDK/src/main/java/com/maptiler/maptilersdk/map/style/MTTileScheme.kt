/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A scheme used for tiles.
 */
@Serializable
enum class MTTileScheme {
    /**
     * XYZ Format.
     */
    @SerialName("xyz")
    XYZ,

    /**
     * TMS Format.
     */
    @SerialName("tms")
    TMS,

    ;

    override fun toString() = serializer().descriptor.getElementName(ordinal)
}
