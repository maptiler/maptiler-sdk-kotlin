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
import java.net.URL

/**
 * Provides estimation for offline pack size and resource counts.
 */
class MTOfflineEstimator {
    companion object {
        // Average sizes in bytes for estimation purposes
        private const val AVERAGE_TILE_SIZE_VECTOR: Long = 25_000 // 25 KB
        private const val AVERAGE_TILE_SIZE_RASTER: Long = 150_000 // 150 KB
        private const val AVERAGE_SPRITE_SIZE: Long = 50_000 // 50 KB
        private const val AVERAGE_GLYPH_RANGE_SIZE: Long = 15_000 // 15 KB
        private const val AVERAGE_STYLE_SIZE: Long = 100_000 // 100 KB
    }

    /**
     * Estimates the size and resource count for a given region definition.
     *
     * This method may fetch the style JSON and other metadata if needed to provide a more accurate estimate.
     *
     * @param region The definition of the region to estimate.
     * @return An [MTPackStats] object containing the estimates.
     */
    suspend fun estimatePack(region: MTOfflineRegionDefinition): MTPackStats {
        val zoomRange = MTOfflineZoomRange(region.minZoom, region.maxZoom)

        // For flexible geometries, if padding is not provided, we fall back to a 2000m heuristic
        val isFlexible = region.geometry !is MTOfflineRegionGeometry.BoundingBox
        val paddingMeters = region.padding ?: if (isFlexible) 2000.0 else null

        val apiKey = MTConfig.apiKey
        val styleUrl = region.referenceStyle.fetchStyleUrl(region.styleVariant, apiKey)

        if (styleUrl == null) {
            // If no style URL, we can only estimate based on tile count if we assume a single vector source
            val tileCount =
                MTTileMath.estimateTileCount(
                    region.geometry,
                    zoomRange,
                    paddingMeters,
                )
            return MTPackStats(
                expectedSize = tileCount.toLong() * AVERAGE_TILE_SIZE_VECTOR,
                resourceCount = tileCount,
                tilesPerSource = mapOf("default" to tileCount),
            )
        }

        return try {
            val styleData = fetchString(styleUrl)
            val parser = MTStyleParser()
            val dependencies = parser.extractDependencies(styleData)

            var totalSize: Long = AVERAGE_STYLE_SIZE
            var totalResourceCount = 1 // Style itself

            val spriteCount = dependencies.sprites.size * 2 // JSON + PNG
            totalResourceCount += spriteCount
            totalSize += spriteCount.toLong() * AVERAGE_SPRITE_SIZE

            // Rough estimate: we download common glyph ranges for each font stack
            // MTGlyphHelper generates 256 ranges for the default 65535 index (0-255 chunks).
            val glyphRangesPerFontStack = 256
            val glyphCount = dependencies.fontStacks.size * glyphRangesPerFontStack
            totalResourceCount += glyphCount
            totalSize += glyphCount.toLong() * AVERAGE_GLYPH_RANGE_SIZE

            val tilesPerSource = mutableMapOf<String, Int>()

            for (source in dependencies.sources) {
                val resolved = resolveTemplateUrl(source, apiKey)

                // Source-specific zoom range constraints
                val sourceMinZoom = maxOf(region.minZoom, resolved.minZoom)
                val sourceMaxZoom = minOf(region.maxZoom, resolved.maxZoom)

                if (sourceMinZoom <= sourceMaxZoom) {
                    val sourceZoomRange = MTOfflineZoomRange(sourceMinZoom, sourceMaxZoom)
                    val tileCount =
                        MTTileMath.estimateTileCount(
                            region.geometry,
                            sourceZoomRange,
                            paddingMeters,
                        )
                    tilesPerSource[source.id] = tileCount
                    totalResourceCount += tileCount

                    if (source.type?.contains("raster") == true) {
                        totalSize += tileCount.toLong() * AVERAGE_TILE_SIZE_RASTER
                    } else {
                        // Default to vector
                        totalSize += tileCount.toLong() * AVERAGE_TILE_SIZE_VECTOR
                    }
                }
            }

            MTPackStats(
                expectedSize = totalSize,
                resourceCount = totalResourceCount,
                tilesPerSource = tilesPerSource,
            )
        } catch (e: Exception) {
            // Fallback to basic estimation if parsing fails
            val tileCount =
                MTTileMath.estimateTileCount(
                    region.geometry,
                    zoomRange,
                    paddingMeters,
                )
            MTPackStats(
                expectedSize = tileCount.toLong() * AVERAGE_TILE_SIZE_VECTOR,
                resourceCount = tileCount,
                tilesPerSource = mapOf("default" to tileCount),
            )
        }
    }

    private suspend fun fetchString(url: URL): String =
        withContext(Dispatchers.IO) {
            url.readText()
        }

    private data class TemplateInfo(
        val template: String,
        val minZoom: Int,
        val maxZoom: Int,
    )

    private suspend fun resolveTemplateUrl(
        source: MTStyleSource,
        apiKey: String,
    ): TemplateInfo {
        val sourceMin = source.minZoom ?: 0
        val sourceMax = source.maxZoom ?: 22

        if (!source.tiles.isNullOrEmpty()) {
            return TemplateInfo(source.tiles.first(), sourceMin, sourceMax)
        }

        val urlStr = source.url ?: return TemplateInfo("", sourceMin, sourceMax)
        val url =
            try {
                val normalizedUrlStr =
                    if (urlStr.contains("?")) {
                        if (urlStr.contains("key=")) urlStr else "$urlStr&key=$apiKey"
                    } else {
                        "$urlStr?key=$apiKey"
                    }
                URL(normalizedUrlStr)
            } catch (e: Exception) {
                return TemplateInfo("", sourceMin, sourceMax)
            }

        return try {
            val tileJsonData = fetchString(url)
            val tileJson = JsonConfig.json.decodeFromString<MTTileJSON>(tileJsonData)
            TemplateInfo(
                template = tileJson.preferredTileUrlTemplate ?: "",
                minZoom = tileJson.minzoom,
                maxZoom = tileJson.maxzoom,
            )
        } catch (e: Exception) {
            TemplateInfo("", sourceMin, sourceMax)
        }
    }
}
