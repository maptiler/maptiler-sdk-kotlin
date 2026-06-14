/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import kotlinx.serialization.Serializable

/**
 * Represents a downloadable resource.
 */
@Serializable
internal data class MTMapResource(
    val url: String,
    val destinationPath: String,
    val size: Long? = null,
)
