/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style

/**
 * The proxy object for the current map style.
 *
 * Set of convenience methods for style, sources and layers manipulation.
 * MTStyle is nil until map loading is complete.
 */
class MTStyle(
    reference: MTMapReferenceStyle,
    variant: MTMapStyleVariant? = null,
) {
    /**
     * Current reference style of the map object.
     */
    var referenceStyle: MTMapReferenceStyle = MTMapReferenceStyle.STREETS
        private set

    /**
     * Current style variant of the map object.
     */
    var styleVariant: MTMapStyleVariant? = null
        private set

    init {
        referenceStyle = reference
        styleVariant = variant
    }

    /**
     * Returns variants for the current reference style if they exist.
     */
    fun getVariantsForCurrentReferenceStyle(): List<MTMapStyleVariant>? = this.referenceStyle.getVariants()

    /**
     * Returns variants for the provided reference style if they exist.
     *
     * @param reference Reference style for which to get variants.
     */
    fun getVariantsForReferenceStyle(reference: MTMapReferenceStyle): List<MTMapStyleVariant>? = reference.getVariants()
}
