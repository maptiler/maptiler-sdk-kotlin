/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import kotlinx.serialization.Serializable

/**
 * Experimental options controlling tile preloading.
 *
 * Pass an instance as `experimentalPreload` in camera animation options (fly, ease, pan, zoom, fit bounds) to fetch
 * the tiles along the camera path before the animation starts. The animation begins once preloading completes.
 *
 * Preloading only works when the map was created with
 * [com.maptiler.maptilersdk.map.MTMapOptions.useExperimentalTilePreloading] set to `true`.
 *
 * **API Key Usage**: every preloaded tile is a request that counts against your MapTiler Cloud API key quota.
 */
@Serializable
data class MTTilePreloadOptions(
    /**
     * Maximum number of tiles to fetch across all sources. Tiles beyond this limit are skipped.
     *
     * Defaults to 512 when null.
     */
    val maxTiles: Int? = null,
)
