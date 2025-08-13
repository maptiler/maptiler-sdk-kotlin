/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style

/**
 * Represents the exceptions raised by the MTStyle object.
 */
enum class MTStyleError {
    /**
     * Source with the same id already added to the map.
     */
    SOURCE_ALREADY_EXISTS,

    /**
     * Source does not exist in the map.
     */
    SOURCE_NOT_FOUND,

    /**
     * Layer with the same id already added to the map.
     */
    LAYER_ALREADY_EXISTS,

    /**
     * Layer does not exist in the map.
     */
    LAYER_NOT_FOUND,
}
