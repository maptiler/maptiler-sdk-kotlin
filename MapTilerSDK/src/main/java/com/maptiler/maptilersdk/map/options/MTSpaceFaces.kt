/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import kotlinx.serialization.Serializable

/**
 * Definition of a cubemap using explicit face URLs.
 *
 * All fields should point to accessible image URLs.
 */
@Serializable
class MTSpaceFaces(
    val pX: String,
    val nX: String,
    val pY: String,
    val nY: String,
    val pZ: String,
    val nZ: String,
)
