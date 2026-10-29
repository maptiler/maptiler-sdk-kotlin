/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.net.Uri
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Runs the shared estimator fixtures (maptiler-offline-estimator/fixtures) that every platform must agree on.
 *
 * Each case is checked three ways: against the expected counts in the fixture, against the set-based reference
 * implementation the planner used before, and against the number of resources the planner puts in the manifest.
 */
class MTOfflineEstimatorFixtureTest {
    // Glyph URLs percent-encode font stacks with android.net.Uri, which is not available in unit tests.
    @Before
    fun mockUriEncoding() {
        mockkStatic(Uri::class)
        every { Uri.encode(any()) } answers { java.net.URLEncoder.encode(firstArg<String>(), "UTF-8") }
    }

    @After
    fun unmockUriEncoding() {
        unmockkStatic(Uri::class)
    }

    @Serializable
    data class FixtureGeometry(
        val type: String,
        val bbox: List<Double>? = null,
        val coordinates: List<List<Double>>? = null,
    ) {
        fun toGeometry(): MTOfflineRegionGeometry {
            val points = coordinates.orEmpty().map { LngLat(it[0], it[1]) }
            return when (type) {
                "bbox" -> MTOfflineRegionGeometry.BoundingBox(MTBoundingBox(bbox!![0], bbox[1], bbox[2], bbox[3]))
                "polygon" -> MTOfflineRegionGeometry.Polygon(points)
                else -> MTOfflineRegionGeometry.Route(points)
            }
        }
    }

    @Serializable
    data class FixtureRegion(
        val geometry: FixtureGeometry,
        val minZoom: Int,
        val maxZoom: Int,
        val padding: Double? = null,
        val terrain: Boolean? = null,
        val maxTileCount: Int? = null,
        // "all", "latinGreekCyrillic" (default) or a list of range start code points.
        val glyphRanges: JsonElement? = null,
    ) {
        private fun glyphRangesValue(): MTOfflineGlyphRanges =
            when {
                glyphRanges == null -> MTOfflineGlyphRanges.LatinGreekCyrillic
                glyphRanges is JsonPrimitive && glyphRanges.content == "all" -> MTOfflineGlyphRanges.All
                glyphRanges is JsonPrimitive -> MTOfflineGlyphRanges.LatinGreekCyrillic
                else -> MTOfflineGlyphRanges.Custom(glyphRanges.jsonArray.map { it.jsonPrimitive.int })
            }

        fun toDefinition() =
            MTOfflineRegionDefinition(
                geometry = geometry.toGeometry(),
                minZoom = minZoom,
                maxZoom = maxZoom,
                referenceStyle = MTMapReferenceStyle.STREETS,
                maxTileCount = maxTileCount,
                padding = padding,
                isTerrainEnabled = terrain ?: false,
                glyphRanges = glyphRangesValue(),
            )
    }

    @Serializable
    data class FixtureCase(
        val name: String,
        val metadata: String,
        val region: FixtureRegion,
    )

    @Serializable
    data class Fixtures(
        val metadata: Map<String, MTOfflineStyleMetadata>,
        val cases: List<FixtureCase>,
    )

    @Serializable
    data class SourceCounts(
        val minZoom: Int,
        val maxZoom: Int,
        val tilesPerZoom: Map<String, Long>,
    )

    @Serializable
    data class Expected(
        val sources: Map<String, SourceCounts>,
        val glyphs: Int,
        val sprites: Int,
        val style: Int,
        val total: Long,
    )

    @Serializable
    data class Expectations(
        val cases: Map<String, Expected>,
    )

    private fun resource(name: String): String =
        requireNotNull(javaClass.getResourceAsStream("/fixtures/$name")) { "Missing fixture $name" }
            .bufferedReader()
            .use { it.readText() }

    @Test
    fun testFixtures() {
        val fixtures = JsonConfig.json.decodeFromString<Fixtures>(resource("offline-estimator-fixtures.json"))
        val expectations = JsonConfig.json.decodeFromString<Expectations>(resource("offline-estimator-expected.json"))

        for (fixtureCase in fixtures.cases) {
            val metadata = requireNotNull(fixtures.metadata[fixtureCase.metadata])
            val region = fixtureCase.region.toDefinition()

            val stats = MTOfflineEstimator().estimatePack(region, metadata)
            val actual = stats.toExpected()

            assertEquals("${fixtureCase.name}: reference", referenceCounts(region, metadata), actual)

            val expected = expectations.cases[fixtureCase.name]
            assertNotNull("${fixtureCase.name}: missing expected counts", expected)
            assertEquals(fixtureCase.name, expected, actual)

            // The manifest must contain exactly the estimated resources, without duplicates.
            val plan = MTOfflineResourcePlan(region, metadata)
            val tiles = plan.tileResources()
            assertEquals(fixtureCase.name, stats.tileCount, tiles.size.toLong())
            assertEquals(fixtureCase.name, tiles.size, tiles.map { it.destinationPath }.toSet().size)
            assertEquals(fixtureCase.name, stats.glyphCount, plan.glyphResources().size)
            assertEquals(fixtureCase.name, stats.spriteCount, plan.spriteResources().size)
        }
    }

    private fun MTPackStats.toExpected() =
        Expected(
            sources =
                sources.associate { source ->
                    source.id to
                        SourceCounts(
                            minZoom = source.minZoom,
                            maxZoom = source.maxZoom,
                            tilesPerZoom = source.tilesPerZoom.mapKeys { it.key.toString() },
                        )
                },
            glyphs = glyphCount,
            sprites = spriteCount,
            style = styleCount,
            total = totalResourceCount,
        )

    /**
     * Counts computed with the set-based tile helpers, following the planner's original resource rules.
     */
    private fun referenceCounts(
        region: MTOfflineRegionDefinition,
        metadata: MTOfflineStyleMetadata,
    ): Expected {
        val sources = metadata.sources.toMutableList()
        val terrain = metadata.terrainSource
        if (region.isTerrainEnabled && sources.none { it.type == "raster-dem" } && terrain != null) {
            sources.add(terrain)
        }
        val isFlexible = region.geometry !is MTOfflineRegionGeometry.BoundingBox
        val paddingMeters = region.padding ?: if (isFlexible) 2000.0 else null

        val counts = mutableMapOf<String, SourceCounts>()
        for (source in sources) {
            var effectiveMin = maxOf(region.minZoom, source.minZoom)
            var effectiveMax = minOf(region.maxZoom, source.maxZoom)
            if (region.minZoom > source.maxZoom) {
                effectiveMin = source.maxZoom
                effectiveMax = source.maxZoom
            }
            if (effectiveMin > effectiveMax) continue

            val tilesPerZoom = mutableMapOf<String, Long>()
            for (zoom in effectiveMin..effectiveMax) {
                val geometry = region.geometry
                tilesPerZoom[zoom.toString()] =
                    if (geometry is MTOfflineRegionGeometry.BoundingBox) {
                        val tiles = mutableSetOf<MTTileIndex>()
                        for (box in geometry.bbox.normalizedAndSplit()) {
                            val (xs, ys) = MTTileMath.tileRanges(box, zoom, paddingMeters)
                            for (x in xs) for (y in ys) tiles.add(MTTileIndex(x, y, zoom))
                        }
                        tiles.size.toLong()
                    } else {
                        MTTileMath.tiles(geometry, zoom, paddingMeters).size.toLong()
                    }
            }
            counts[source.id] = SourceCounts(effectiveMin, effectiveMax, tilesPerZoom)
        }

        val glyphs =
            if (metadata.glyphsTemplate == null) 0 else metadata.fontStacks.size * region.glyphRanges.rangeStarts.size
        val sprites = metadata.sprites.size * 4
        val tiles = counts.values.sumOf { it.tilesPerZoom.values.sum() }
        return Expected(counts, glyphs, sprites, 1, tiles + glyphs + sprites + 1)
    }
}
