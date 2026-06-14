/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.helpers.JsonConfig
import kotlinx.serialization.Serializable

/**
 * Manifest v1 representing the planned offline region download.
 */
@Serializable
internal data class MTManifest(
    /**
     * The format version identifier.
     */
    val version: String = "1",
    /**
     * The original inputs used to generate this manifest.
     */
    val metadata: MTManifestMetadata,
    /**
     * The style JSON resource.
     */
    var style: MTMapResource? = null,
    /**
     * The list of tile resources to download.
     */
    var tiles: List<MTMapResource> = emptyList(),
    /**
     * The list of glyph resources.
     */
    var glyphs: List<MTMapResource> = emptyList(),
    /**
     * The list of sprite resources.
     */
    var sprites: List<MTMapResource> = emptyList(),
) {
    /**
     * Encodes this manifest to a JSON string.
     */
    fun toJson(): String = JsonConfig.json.encodeToString(serializer(), this)

    companion object {
        /**
         * Decodes manifest from a JSON string.
         */
        fun fromJson(json: String): MTManifest = JsonConfig.json.decodeFromString(serializer(), json)
    }
}
