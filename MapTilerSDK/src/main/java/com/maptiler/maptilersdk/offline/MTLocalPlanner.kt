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
 * A local implementation of the [MTOfflinePlanner] interface.
 */
internal class MTLocalPlanner : MTOfflinePlanner {
    override suspend fun estimate(definition: MTOfflineRegionDefinition): MTTileEstimate {
        validate(definition)

        val estimator = MTOfflineEstimator()
        val stats = estimator.estimatePack(definition)

        val globalLimit = MTOfflineConfiguration.effectiveGlobalLimit
        val packLimit = definition.maxTileCount ?: Int.MAX_VALUE
        val effectiveLimit = minOf(globalLimit, packLimit)

        if (stats.resourceCount > effectiveLimit) {
            throw MTOfflineError.ExceedsMaximumTileCount(
                limit = effectiveLimit,
                requested = stats.resourceCount,
            )
        }

        return MTTileEstimate(stats)
    }

    override suspend fun generateManifest(definition: MTOfflineRegionDefinition): MTManifest {
        validate(definition)

        val isFlexible = definition.geometry !is MTOfflineRegionGeometry.BoundingBox
        val paddingMeters = definition.padding ?: if (isFlexible) 2000.0 else null

        // For non-rectangular geometries, slightly pad the bounding box stored in the manifest.
        var manifestBbox = definition.geometry.bbox
        if (paddingMeters != null && paddingMeters > 0 && isFlexible) {
            manifestBbox = manifestBbox.expanded(paddingMeters)
        }

        val metadata =
            MTManifestMetadata(
                referenceStyle = definition.referenceStyle,
                styleVariant = definition.styleVariant,
                bbox = manifestBbox,
                minZoom = definition.minZoom,
                maxZoom = definition.maxZoom,
                pixelRatio = definition.pixelRatio,
            )

        val apiKey = MTConfig.apiKey
        val styleUrl =
            definition.referenceStyle.fetchStyleUrl(definition.styleVariant, apiKey)
                ?: throw MTOfflineError.InvalidRegion

        val (styleResource, dependencies) = resolveStyle(styleUrl)

        val tileResources =
            generateTileResources(
                geometry = definition.geometry,
                minZoom = definition.minZoom,
                maxZoom = definition.maxZoom,
                paddingMeters = paddingMeters,
                dependencies = dependencies,
            )

        val glyphResources = generateGlyphResources(dependencies)
        val spriteResources = generateSpriteResources(dependencies)

        val totalCount = tileResources.size + glyphResources.size + spriteResources.size + 1 // +1 for style

        val globalLimit = MTOfflineConfiguration.effectiveGlobalLimit
        val packLimit = definition.maxTileCount ?: Int.MAX_VALUE
        val effectiveLimit = minOf(globalLimit, packLimit)

        if (totalCount > effectiveLimit) {
            throw MTOfflineError.ExceedsMaximumTileCount(
                limit = effectiveLimit,
                requested = totalCount,
            )
        }

        val manifest =
            MTManifest(
                metadata = metadata,
                style = styleResource,
                tiles = tileResources,
                glyphs = glyphResources,
                sprites = spriteResources,
            )

        if (manifest.tiles.isEmpty()) {
            throw MTOfflineError.InvalidRegion
        }

        return manifest
    }

    private fun validate(definition: MTOfflineRegionDefinition) {
        if (definition.minZoom < 0 || definition.maxZoom > 22 || definition.minZoom > definition.maxZoom) {
            throw MTOfflineError.InvalidRegion
        }

        val bbox = definition.geometry.bbox
        if (bbox.minLat < -85.051129 ||
            bbox.maxLat > 85.051129 ||
            bbox.minLon < -180.0 ||
            bbox.maxLon > 180.0 ||
            bbox.minLat > bbox.maxLat
        ) {
            throw MTOfflineError.InvalidRegion
        }
    }

    private suspend fun resolveStyle(url: URL): Pair<MTMapResource, MTStyleDependencies> {
        val normalizedUrl = MTURLNormalizer.normalize(url.toString())

        val data =
            withContext(Dispatchers.IO) {
                val request = okhttp3.Request.Builder().url(normalizedUrl).build()
                MTOfflineHttpClient.client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw MTOfflineError.BadResponse(response.code)
                    response.body?.string() ?: throw MTOfflineError.DownloadFailed(java.io.IOException("Empty body"))
                }
            }

        val resource = MTMapResource(url = url.toString(), destinationPath = "style.json")
        val parser = MTStyleParser()
        val dependencies = parser.extractDependencies(data)
        return Pair(resource, dependencies)
    }

    private suspend fun generateTileResources(
        geometry: MTOfflineRegionGeometry,
        minZoom: Int,
        maxZoom: Int,
        paddingMeters: Double?,
        dependencies: MTStyleDependencies,
    ): List<MTMapResource> {
        val resources = mutableListOf<MTMapResource>()

        for (source in dependencies.sources) {
            val resolved = resolveTemplateUrl(source)
            val template = resolved.template

            var effMin = maxOf(minZoom, resolved.minZoom)
            var effMax = minOf(maxZoom, resolved.maxZoom)

            if (minZoom > resolved.maxZoom) {
                effMin = resolved.maxZoom
                effMax = resolved.maxZoom
            }

            if (effMin > effMax) continue

            val ext = detectExtension(template)

            if (geometry is MTOfflineRegionGeometry.BoundingBox) {
                val isTms = template.contains("{-y}")
                val scheme = if (isTms) "tms" else "xyz"
                val inputs =
                    MTOfflineCoverageInputs(
                        scheme = scheme,
                        zoomRange = MTOfflineZoomRange(effMin, effMax),
                    )
                val generator = MTOfflineCoverageGenerator(geometry.bbox, inputs, paddingMeters)

                for (tile in generator) {
                    val z = tile.z
                    val x = tile.x
                    val y = tile.y

                    val tileUrlStr =
                        template
                            .replace("{z}", z.toString())
                            .replace("{x}", x.toString())
                            .replace("{y}", y.toString())
                            .replace("{-y}", y.toString()) // Generator already flips Y if scheme is tms

                    val destPath = "tiles/${source.id}/$z/$x/$y.$ext"
                    resources.add(MTMapResource(url = tileUrlStr, destinationPath = destPath))
                }
                continue
            }

            // For route/polygon, we need all tiles anyway for union.
            val tiles = mutableSetOf<MTTileIndex>()
            for (z in effMin..effMax) {
                tiles.addAll(MTTileMath.tiles(geometry, z, paddingMeters))
            }

            for (tile in tiles) {
                val z = tile.z
                val x = tile.x
                val y = tile.y

                val resolvedY =
                    if (template.contains("{-y}")) {
                        MTTileMath.flipYCoordinate(y, z)
                    } else {
                        y
                    }

                val tileUrlStr =
                    template
                        .replace("{z}", z.toString())
                        .replace("{x}", x.toString())
                        .replace("{y}", resolvedY.toString())
                        .replace("{-y}", resolvedY.toString())

                val destPath = "tiles/${source.id}/$z/$x/$y.$ext"
                resources.add(MTMapResource(url = tileUrlStr, destinationPath = destPath))
            }
        }
        return resources
    }

    private data class TemplateInfo(
        val template: String,
        val minZoom: Int,
        val maxZoom: Int,
    )

    private suspend fun resolveTemplateUrl(source: MTStyleSource): TemplateInfo {
        val sourceMin = source.minZoom ?: 0
        val sourceMax = source.maxZoom ?: 22

        if (!source.tiles.isNullOrEmpty()) {
            return TemplateInfo(source.tiles.first(), sourceMin, sourceMax)
        }

        val urlStr = source.url ?: throw MTOfflineError.InvalidRegion
        val normalizedUrl = MTURLNormalizer.normalize(urlStr)

        val tileJsonData =
            withContext(Dispatchers.IO) {
                val request = okhttp3.Request.Builder().url(normalizedUrl).build()
                MTOfflineHttpClient.client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw MTOfflineError.BadResponse(response.code)
                    response.body?.string() ?: throw MTOfflineError.DownloadFailed(java.io.IOException("Empty body"))
                }
            }

        val tileJson = JsonConfig.json.decodeFromString<MTTileJSON>(tileJsonData)
        return TemplateInfo(
            template = tileJson.preferredTileUrlTemplate ?: throw MTOfflineError.InvalidRegion,
            minZoom = tileJson.minzoom,
            maxZoom = tileJson.maxzoom,
        )
    }

    private fun detectExtension(template: String): String {
        val lower = template.lowercase()
        return when {
            lower.contains(".png") -> "png"
            lower.contains(".jpg") || lower.contains(".jpeg") -> "jpg"
            lower.contains(".webp") -> "webp"
            else -> "pbf"
        }
    }

    private fun generateGlyphResources(dependencies: MTStyleDependencies): List<MTMapResource> {
        val template = dependencies.glyphsTemplate ?: return emptyList()
        val fontStacks = dependencies.fontStacks
        if (fontStacks.isEmpty()) return emptyList()

        val resources = mutableListOf<MTMapResource>()
        val ranges = MTGlyphHelper.generateRanges()

        for (fontStack in fontStacks) {
            val fontStackStr = fontStack.joinToString(",")
            for (range in ranges) {
                val rangeStr = range.toString()
                val urlStr = MTGlyphHelper.format(template, fontStackStr, rangeStr)
                val destPath = "glyphs/$fontStackStr/$rangeStr.pbf"
                resources.add(MTMapResource(url = urlStr, destinationPath = destPath))
            }
        }
        return resources
    }

    private fun generateSpriteResources(dependencies: MTStyleDependencies): List<MTMapResource> {
        val resources = mutableListOf<MTMapResource>()
        for (sprite in dependencies.sprites) {
            val baseUrl = sprite.url

            val variants = listOf("", "@2x")
            val formats = listOf(".json", ".png")

            for (variant in variants) {
                for (format in formats) {
                    val url = "$baseUrl$variant$format"
                    val filename =
                        if (sprite.id == "default") {
                            "sprite$variant$format"
                        } else {
                            "sprite-${sprite.id}$variant$format"
                        }
                    resources.add(MTMapResource(url = url, destinationPath = filename))
                }
            }
        }
        return resources
    }
}
