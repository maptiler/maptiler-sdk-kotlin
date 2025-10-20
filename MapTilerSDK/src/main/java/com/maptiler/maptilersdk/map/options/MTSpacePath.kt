/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import kotlinx.serialization.Serializable

/**
 * Definition of a cubemap path. Client must provide a base URL; files are
 * expected to be named px, nx, py, ny, pz, nz with the specified format.
 */
@Serializable
class MTSpacePath(
    val baseUrl: String,
    val format: String? = null,
)
