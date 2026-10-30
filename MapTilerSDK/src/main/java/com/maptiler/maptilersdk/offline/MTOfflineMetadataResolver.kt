/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.helpers.JsonConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Resolves the style metadata of a region by fetching its style JSON and source TileJSONs.
 *
 * Results are cached for a short time, so estimating a pack and then downloading it fetches the style
 * and TileJSONs only once. Tiles are never requested here.
 *
 * @param fetch Loads a URL and returns its body. Defaults to an authenticated request through the offline HTTP client.
 */
internal class MTOfflineMetadataResolver(
    private val fetch: suspend (String) -> String = ::fetchString,
    private val timeToLiveMillis: Long = 15 * 60 * 1000L,
) {
    private val cache = mutableMapOf<String, Pair<MTOfflineStyleMetadata, Long>>()

    suspend fun metadata(definition: MTOfflineRegionDefinition): MTOfflineStyleMetadata {
        val apiKey = MTConfig.apiKey
        val styleUrl =
            definition.referenceStyle.fetchStyleUrl(definition.styleVariant, apiKey)?.toString()
                ?: throw MTOfflineError.InvalidRegion
        val includeTerrain = definition.isTerrainEnabled
        val cacheKey = "$styleUrl|terrain=$includeTerrain"

        synchronized(cache) {
            cache[cacheKey]?.let { (metadata, resolvedAt) ->
                if (System.currentTimeMillis() - resolvedAt < timeToLiveMillis) return metadata
            }
        }

        val dependencies = MTStyleParser().extractDependencies(fetch(styleUrl))
        val sources = dependencies.sources.map { resolveSource(it) }

        val terrainSource =
            if (includeTerrain && sources.none { it.type == "raster-dem" }) {
                resolveSource(
                    MTStyleSource(
                        id = "maptiler-terrain",
                        type = "raster-dem",
                        url = "${MTConfig.apiURL}tiles/terrain-rgb-v2/tiles.json?key=$apiKey",
                    ),
                )
            } else {
                null
            }

        val metadata =
            MTOfflineStyleMetadata(
                styleUrl = styleUrl,
                sources = sources,
                terrainSource = terrainSource,
                glyphsTemplate = dependencies.glyphsTemplate,
                fontStacks = dependencies.fontStacks,
                sprites = dependencies.sprites.map { MTOfflineSpriteMetadata(id = it.id, url = it.url) },
            )
        synchronized(cache) {
            cache[cacheKey] = metadata to System.currentTimeMillis()
        }
        return metadata
    }

    /**
     * Resolves a source's tile URL template and zoom limits. Inline `tiles` use the style's zoom limits;
     * otherwise the TileJSON is fetched and its zoom limits take precedence.
     */
    private suspend fun resolveSource(source: MTStyleSource): MTOfflineSourceMetadata {
        if (!source.tiles.isNullOrEmpty()) {
            return MTOfflineSourceMetadata(
                id = source.id,
                type = source.type,
                tileUrlTemplate = source.tiles.first(),
                minZoom = source.minZoom ?: 0,
                maxZoom = source.maxZoom ?: 22,
            )
        }

        val url = source.url ?: throw MTOfflineError.InvalidRegion
        val tileJson = JsonConfig.json.decodeFromString<MTTileJSON>(fetch(url))
        return MTOfflineSourceMetadata(
            id = source.id,
            type = source.type,
            tileUrlTemplate = tileJson.preferredTileUrlTemplate ?: throw MTOfflineError.InvalidRegion,
            minZoom = tileJson.minzoom,
            maxZoom = tileJson.maxzoom,
        )
    }

    companion object {
        val shared = MTOfflineMetadataResolver()

        private suspend fun fetchString(url: String): String =
            withContext(Dispatchers.IO) {
                val request = okhttp3.Request.Builder().url(MTURLNormalizer.normalize(url)).build()
                MTOfflineHttpClient.client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw MTOfflineError.BadResponse(response.code)
                    response.body?.string() ?: throw MTOfflineError.DownloadFailed(java.io.IOException("Empty body"))
                }
            }
    }
}
