/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import kotlinx.serialization.Serializable

/**
 * Options for setting padding on calls to map methods.
 */
@Serializable
data class MTPaddingOptions(
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double,
)
