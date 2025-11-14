/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.image

/**
 * Options used when registering an image in the current map style.
 *
 * @param sdf Whether the image should be interpreted as a signed distance field.
 * @param pixelRatio Override pixel ratio for the image. Must be greater than 0 when provided.
 */
data class MTAddImageOptions(
    val sdf: Boolean? = null,
    val pixelRatio: Double? = null,
) {
    init {
        if (pixelRatio != null && pixelRatio <= 0.0) {
            throw IllegalArgumentException("pixelRatio must be greater than 0.0")
        }
    }
}
