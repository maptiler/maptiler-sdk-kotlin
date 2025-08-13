/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.types

import kotlinx.serialization.Serializable

/**
 * The style spec representation of the source if the event has a dataType of source .
 */
@Serializable
data class MTSourceData(
    /**
     * Type of the source.
     */
    val type: String? = null,
    /**
     * Url of the source resource.
     */
    val url: String? = null,
    /**
     * Attribution string.
     */
    val attribution: String? = null,
)
