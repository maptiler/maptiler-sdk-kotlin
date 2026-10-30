/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A tile source of a style, resolved to the zoom range and URL template the offline planner uses.
 */
@Serializable
data class MTOfflineSourceMetadata(
    /** The source identifier from the style. */
    val id: String,
    /** The source type (`vector`, `raster` or `raster-dem`). */
    val type: String? = null,
    /** The tile URL template used for downloads. */
    @SerialName("tileURLTemplate")
    val tileUrlTemplate: String,
    /** The minimum zoom level provided by the source. */
    val minZoom: Int,
    /** The maximum zoom level provided by the source. */
    val maxZoom: Int,
)

/**
 * A sprite referenced by a style.
 */
@Serializable
data class MTOfflineSpriteMetadata(
    /** The sprite identifier (`default` for a single sprite URL). */
    val id: String,
    /** The sprite base URL, without extension. */
    val url: String,
)

/**
 * Everything about a style that determines which resources an offline pack downloads.
 *
 * Resolving it requires fetching the style JSON and the TileJSON of each source. Once resolved, it can be stored
 * and passed to [MTOfflineEstimator.estimatePack] to estimate packs without any network requests.
 */
@Serializable
data class MTOfflineStyleMetadata(
    /** The style JSON URL. */
    @SerialName("styleURL")
    val styleUrl: String? = null,
    /** The tile sources of the style. */
    val sources: List<MTOfflineSourceMetadata>,
    /** The terrain source added when terrain is enabled and the style has no `raster-dem` source. */
    val terrainSource: MTOfflineSourceMetadata? = null,
    /** The glyphs URL template. */
    val glyphsTemplate: String? = null,
    /** The font stacks used by the style's symbol layers. */
    val fontStacks: List<List<String>> = emptyList(),
    /** The sprites of the style. */
    val sprites: List<MTOfflineSpriteMetadata> = emptyList(),
) {
    /**
     * A copy without API keys and session ids in its URLs, so it can be stored or shared safely.
     *
     * Estimates only use the URL paths, so they are the same for the redacted copy.
     */
    internal fun redacted(): MTOfflineStyleMetadata =
        copy(
            styleUrl = styleUrl?.let(::withoutCredentials),
            sources = sources.map { it.copy(tileUrlTemplate = withoutCredentials(it.tileUrlTemplate)) },
            terrainSource = terrainSource?.let { it.copy(tileUrlTemplate = withoutCredentials(it.tileUrlTemplate)) },
            glyphsTemplate = glyphsTemplate?.let(::withoutCredentials),
            sprites = sprites.map { it.copy(url = withoutCredentials(it.url)) },
        )

    private fun withoutCredentials(url: String): String {
        val parts = url.split("?", limit = 2)
        if (parts.size != 2) {
            return url
        }

        val query = parts[1].split("&").filter { it.isNotEmpty() && !it.startsWith("key=") && !it.startsWith("mtsid=") }
        return if (query.isEmpty()) parts[0] else "${parts[0]}?${query.joinToString("&")}"
    }
}
