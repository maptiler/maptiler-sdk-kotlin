/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

/**
 * A local implementation of the [MTOfflinePlanner] interface.
 *
 * @param resolver Resolves (and briefly caches) style metadata for planning.
 */
internal class MTLocalPlanner(
    private val resolver: MTOfflineMetadataResolver = MTOfflineMetadataResolver.shared,
) : MTOfflinePlanner {
    override suspend fun estimate(definition: MTOfflineRegionDefinition): MTTileEstimate {
        val stats = MTOfflineEstimator(resolver).estimatePack(definition)

        if (stats.exceedsLimit) {
            throw MTOfflineError.ExceedsMaximumTileCount(
                limit = stats.resourceLimit,
                requested = stats.resourceCount,
            )
        }

        return MTTileEstimate(stats)
    }

    override suspend fun generateManifest(definition: MTOfflineRegionDefinition): MTManifest {
        definition.validateForOffline()

        val styleMetadata = resolver.metadata(definition)
        val plan = MTOfflineResourcePlan(definition, styleMetadata)

        if (plan.resourceCount > plan.resourceLimit) {
            throw MTOfflineError.ExceedsMaximumTileCount(
                limit = plan.resourceLimit,
                requested = plan.resourceCount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
            )
        }

        if (plan.tileCount == 0L) {
            throw MTOfflineError.InvalidRegion
        }

        val isFlexible = definition.geometry !is MTOfflineRegionGeometry.BoundingBox
        val paddingMeters = definition.effectivePaddingMeters

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
                isTerrainEnabled = definition.isTerrainEnabled,
            )

        return MTManifest(
            metadata = metadata,
            style = styleMetadata.styleUrl?.let { MTMapResource(url = it, destinationPath = "style.json") },
            tiles = plan.tileResources(),
            glyphs = plan.glyphResources(),
            sprites = plan.spriteResources(),
        )
    }
}
