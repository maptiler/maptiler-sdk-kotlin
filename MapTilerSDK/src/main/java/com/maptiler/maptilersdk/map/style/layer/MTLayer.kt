/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.layer

/**
 * Protocol requirements for all types of Layers.
 */
interface MTLayer {
    /**
     * Unique id of the layer.
     */
    var identifier: String

    /**
     * Type of the layer.
     */
    val type: MTLayerType

    /**
     * Identifier of the source.
     */
    var sourceIdentifier: String

    /**
     * Max zoom of the layer.
     */
    var maxZoom: Double?

    /**
     * Min zoom of the layer.
     */
    var minZoom: Double?

    /**
     * Identifier of the source (main) layer to use.
     */
    var sourceLayer: String?
}
