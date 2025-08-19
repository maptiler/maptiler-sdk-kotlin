/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style

/**
 * Represents the exceptions raised by the MTStyle object.
 */
sealed class MTStyleError(
    message: String,
) : Exception(message) {
    /**
     * Source with the same id already added to the map.
     */
    data object SourceAlreadyExists : MTStyleError("Source already exists") {
        private fun readResolve(): Any = SourceAlreadyExists
    }

    /**
     * Source does not exist in the map.
     */
    data object SourceNotFound : MTStyleError("Source not found") {
        private fun readResolve(): Any = SourceNotFound
    }

    /**
     * Layer with the same id already added to the map.
     */
    data object LayerAlreadyExists : MTStyleError("Layer already exists") {
        private fun readResolve(): Any = LayerAlreadyExists
    }

    /**
     * Layer does not exist in the map.
     */
    data object LayerNotFound : MTStyleError("Layer not found") {
        private fun readResolve(): Any = LayerNotFound
    }
}
