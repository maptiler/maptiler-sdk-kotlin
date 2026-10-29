/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

/**
 * Provides estimation for offline pack size and resource counts.
 *
 * Resource counts are exact: they are computed with the same planning logic the download uses.
 * The estimator never downloads tiles.
 */
class MTOfflineEstimator internal constructor(
    private val resolver: MTOfflineMetadataResolver,
) {
    constructor() : this(MTOfflineMetadataResolver.shared)

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
     * This fetches the style JSON and the TileJSON of each style source (cached for a short time and reused
     * by a subsequent download). No tiles are requested.
     *
     * @param region The definition of the region to estimate.
     * @return An [MTPackStats] object containing the estimates.
     * @throws MTOfflineError If the region is invalid or the style metadata cannot be fetched.
     */
    suspend fun estimatePack(region: MTOfflineRegionDefinition): MTPackStats {
        region.validateForOffline()
        return estimatePack(region, resolver.metadata(region))
    }

    /**
     * Estimates the size and resource count for a given region definition without any network requests.
     *
     * @param region The definition of the region to estimate.
     * @param styleMetadata Previously resolved metadata of the region's style, see [styleMetadata].
     * @return An [MTPackStats] object containing the estimates.
     * @throws MTOfflineError If the region is invalid or terrain is enabled but the metadata has no terrain source.
     */
    fun estimatePack(
        region: MTOfflineRegionDefinition,
        styleMetadata: MTOfflineStyleMetadata,
    ): MTPackStats {
        val plan = MTOfflineResourcePlan(region, styleMetadata)

        var expectedSize = plan.styleCount * AVERAGE_STYLE_SIZE
        expectedSize += plan.spriteCount * AVERAGE_SPRITE_SIZE
        expectedSize += plan.glyphCount * AVERAGE_GLYPH_RANGE_SIZE

        val sources =
            plan.sources.map { sourcePlan ->
                val isRaster = sourcePlan.source.type?.contains("raster") == true
                val averageTileSize = if (isRaster) AVERAGE_TILE_SIZE_RASTER else AVERAGE_TILE_SIZE_VECTOR
                expectedSize += sourcePlan.tileCount * averageTileSize

                MTSourceStats(
                    id = sourcePlan.source.id,
                    type = sourcePlan.source.type,
                    minZoom = sourcePlan.zoomRange.first,
                    maxZoom = sourcePlan.zoomRange.last,
                    tilesPerZoom = sourcePlan.coverages.associate { it.zoom to it.tileCount },
                )
            }

        return MTPackStats(
            expectedSize = expectedSize,
            resourceCount = plan.resourceCount.saturatedToInt(),
            tilesPerSource =
                sources
                    .groupBy { it.id }
                    .mapValues { (_, stats) -> stats.sumOf { it.tileCount }.saturatedToInt() },
            sources = sources,
            glyphCount = plan.glyphCount,
            spriteCount = plan.spriteCount,
            styleCount = plan.styleCount,
            resourceLimit = plan.resourceLimit,
        )
    }

    /**
     * Counts the tiles of a region per source, zoom level and ancestor tile, without any network requests.
     *
     * Tiles at zoom levels up to [groupZoom] are reported individually (each tile is its own group with a count
     * of 1). Tiles at deeper zoom levels are grouped by their ancestor at [groupZoom]. This lets a size model
     * that knows how large each area is at [groupZoom] estimate the bytes of deeper zoom levels.
     *
     * @param region The definition of the region.
     * @param styleMetadata Previously resolved metadata of the region's style.
     * @param groupZoom The zoom level of the ancestor tiles used for grouping.
     * @return The tile groups of every source.
     */
    fun tileGroups(
        region: MTOfflineRegionDefinition,
        styleMetadata: MTOfflineStyleMetadata,
        groupZoom: Int,
    ): List<MTTileGroup> {
        val plan = MTOfflineResourcePlan(region, styleMetadata)
        val groups = mutableListOf<MTTileGroup>()

        for (sourcePlan in plan.sources) {
            for (coverage in sourcePlan.coverages) {
                val ancestorZoom = minOf(coverage.zoom, maxOf(0, groupZoom))
                for ((ancestor, count) in coverage.tileCountsByAncestor(ancestorZoom)) {
                    groups.add(
                        MTTileGroup(
                            sourceId = sourcePlan.source.id,
                            zoom = coverage.zoom,
                            ancestorZoom = ancestorZoom,
                            ancestorX = ancestor.x,
                            ancestorY = ancestor.y,
                            tileCount = count,
                        ),
                    )
                }
            }
        }
        return groups
    }

    /**
     * Resolves the metadata of a region's style by fetching the style JSON and its sources' TileJSON.
     *
     * Store the result to estimate later with [estimatePack] without network requests. The metadata depends on
     * the region's style, variant and terrain setting, not on its geometry or zoom levels. API keys and session ids
     * are removed from its URLs, so it can be stored without exposing your key.
     *
     * @param region A region using the style to resolve.
     * @return The resolved style metadata.
     */
    suspend fun styleMetadata(region: MTOfflineRegionDefinition): MTOfflineStyleMetadata = resolver.metadata(region).redacted()

    private fun Long.saturatedToInt(): Int = coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
}

/**
 * Tiles of one source at one zoom level that share an ancestor tile.
 */
data class MTTileGroup(
    /** The source identifier from the style. */
    val sourceId: String,
    /** The zoom level of the tiles. */
    val zoom: Int,
    /** The zoom level of the shared ancestor tile. Equal to [zoom] when tiles are reported individually. */
    val ancestorZoom: Int,
    /** The X coordinate of the ancestor tile (XYZ scheme). */
    val ancestorX: Int,
    /** The Y coordinate of the ancestor tile (XYZ scheme). */
    val ancestorY: Int,
    /** The number of tiles downloaded under the ancestor tile at [zoom]. */
    val tileCount: Long,
)
