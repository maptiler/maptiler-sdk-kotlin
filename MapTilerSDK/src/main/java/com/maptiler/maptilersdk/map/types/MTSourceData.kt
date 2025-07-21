/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.types

import kotlinx.serialization.Serializable

@Serializable
data class MTSourceData(
    val type: String? = null,
    val url: String? = null,
    val attribution: String? = null,
)
