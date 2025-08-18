/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.source

import java.net.URL

/**
 * Protocol requirements for all tile type sources.
 */
interface MTTileSource : MTSource {
    /**
     * Attribution string.
     */
    var attribution: String?

    /**
     * Bounds of the source.
     */
    var bounds: DoubleArray

    /**
     * Max zoom of the source.
     */
    var maxZoom: Double

    /**
     * Min zoom of the source.
     */
    var minZoom: Double

    /**
     * List of URLs pointing to the tiles resources.
     */
    var tiles: Array<URL>?
}
