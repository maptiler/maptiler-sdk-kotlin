/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

/**
 * The resources an offline pack downloads for a region, derived from resolved style metadata
 * without any network access. The estimator counts it and the planner turns it into a manifest,
 * so estimates always match what is actually downloaded.
 */
internal class MTOfflineResourcePlan(
    val definition: MTOfflineRegionDefinition,
    val metadata: MTOfflineStyleMetadata,
) {
    /**
     * The tiles planned for a single source.
     */
    class SourcePlan(
        val source: MTOfflineSourceMetadata,
        val zoomRange: IntRange,
        val coverages: List<MTTileCoverage>,
    ) {
        val tileCount: Long
            get() = coverages.sumOf { it.tileCount }
    }

    val sources: List<SourcePlan>

    init {
        definition.validateForOffline()

        val sourceList = metadata.sources.toMutableList()
        if (definition.isTerrainEnabled && sourceList.none { it.type == "raster-dem" }) {
            val terrainSource = metadata.terrainSource ?: throw MTOfflineError.MissingKey("terrainSource")
            sourceList.add(terrainSource)
        }

        // Coverage depends only on geometry and zoom, so it is shared between sources.
        val coverageByZoom = mutableMapOf<Int, MTTileCoverage>()
        val paddingMeters = definition.effectivePaddingMeters

        sources =
            sourceList.mapNotNull { source ->
                val zoomRange =
                    effectiveZoomRange(definition.minZoom, definition.maxZoom, source.minZoom, source.maxZoom)
                        ?: return@mapNotNull null
                val coverages =
                    zoomRange.map { zoom ->
                        coverageByZoom.getOrPut(zoom) {
                            MTTileCoverage.make(definition.geometry, zoom, paddingMeters)
                        }
                    }
                SourcePlan(source, zoomRange, coverages)
            }
    }

    val tileCount: Long
        get() = sources.sumOf { it.tileCount }

    val glyphCount: Int
        get() = if (metadata.glyphsTemplate == null) 0 else metadata.fontStacks.size * definition.glyphRanges.rangeStarts.size

    val spriteCount: Int
        get() = metadata.sprites.size * FILES_PER_SPRITE

    val styleCount: Int = 1

    val resourceCount: Long
        get() = tileCount + glyphCount + spriteCount + styleCount

    /**
     * The resource limit that applies to the region: the global limit or the region's own, whichever is lower.
     */
    val resourceLimit: Int
        get() = minOf(MTOfflineConfiguration.effectiveGlobalLimit, definition.maxTileCount ?: Int.MAX_VALUE)

    fun tileResources(): List<MTMapResource> {
        val resources = ArrayList<MTMapResource>(tileCount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
        // Bounding box tiles of TMS sources have always been stored under their flipped Y coordinate,
        // route and polygon tiles under their XYZ coordinate.
        val storesFlippedY = definition.geometry is MTOfflineRegionGeometry.BoundingBox

        for (sourcePlan in sources) {
            val template = sourcePlan.source.tileUrlTemplate
            val ext = detectExtension(template)
            val isTms = template.contains("{-y}")

            for (coverage in sourcePlan.coverages) {
                val z = coverage.zoom
                coverage.forEachTile { x, y ->
                    val resolvedY = if (isTms) MTTileMath.flipYCoordinate(y, z) else y
                    val tileUrl =
                        template
                            .replace("{z}", z.toString())
                            .replace("{x}", x.toString())
                            .replace("{y}", resolvedY.toString())
                            .replace("{-y}", resolvedY.toString())
                    val pathY = if (storesFlippedY) resolvedY else y
                    resources.add(
                        MTMapResource(url = tileUrl, destinationPath = "tiles/${sourcePlan.source.id}/$z/$x/$pathY.$ext"),
                    )
                }
            }
        }
        return resources
    }

    fun glyphResources(): List<MTMapResource> {
        val template = metadata.glyphsTemplate ?: return emptyList()
        val ranges = definition.glyphRanges.ranges

        return metadata.fontStacks.flatMap { fontStack ->
            val fontStackStr = fontStack.joinToString(",")
            ranges.map { range ->
                val rangeStr = range.toString()
                MTMapResource(
                    url = MTGlyphHelper.format(template, fontStackStr, rangeStr),
                    destinationPath = "glyphs/$fontStackStr/$rangeStr.pbf",
                )
            }
        }
    }

    fun spriteResources(): List<MTMapResource> {
        val resources = mutableListOf<MTMapResource>()
        for (sprite in metadata.sprites) {
            for (variant in listOf("", "@2x")) {
                for (format in listOf(".json", ".png")) {
                    val filename =
                        if (sprite.id == "default") {
                            "sprite$variant$format"
                        } else {
                            "sprite-${sprite.id}$variant$format"
                        }
                    resources.add(MTMapResource(url = "${sprite.url}$variant$format", destinationPath = filename))
                }
            }
        }
        return resources
    }

    companion object {
        /** Requests per sprite (JSON and PNG, at 1x and 2x). */
        const val FILES_PER_SPRITE = 4

        /**
         * Intersects the requested zoom range with the source's. When the request starts above the source's
         * maximum zoom, the source's maximum zoom is downloaded so the map can overzoom it.
         */
        fun effectiveZoomRange(
            minZoom: Int,
            maxZoom: Int,
            sourceMinZoom: Int,
            sourceMaxZoom: Int,
        ): IntRange? {
            if (minZoom > sourceMaxZoom) {
                return sourceMaxZoom..sourceMaxZoom
            }
            val effectiveMin = maxOf(minZoom, sourceMinZoom)
            val effectiveMax = minOf(maxZoom, sourceMaxZoom)
            return if (effectiveMin <= effectiveMax) effectiveMin..effectiveMax else null
        }

        fun detectExtension(template: String): String {
            val lower = template.lowercase()
            return when {
                lower.contains(".png") -> "png"
                lower.contains(".jpg") || lower.contains(".jpeg") -> "jpg"
                lower.contains(".webp") -> "webp"
                else -> "pbf"
            }
        }
    }
}

/**
 * The padding applied around the geometry. Routes and polygons default to 2000 meters.
 */
internal val MTOfflineRegionDefinition.effectivePaddingMeters: Double?
    get() = padding ?: if (geometry is MTOfflineRegionGeometry.BoundingBox) null else 2000.0

/**
 * Validates the zoom range and coordinates before planning.
 */
internal fun MTOfflineRegionDefinition.validateForOffline() {
    if (minZoom < 0 || maxZoom > 22 || minZoom > maxZoom) {
        throw MTOfflineError.InvalidRegion
    }

    val bbox = geometry.bbox
    if (bbox.minLat < -85.051129 ||
        bbox.maxLat > 85.051129 ||
        bbox.minLon < -180.0 ||
        bbox.maxLon > 180.0 ||
        bbox.minLat > bbox.maxLat
    ) {
        throw MTOfflineError.InvalidRegion
    }
}
