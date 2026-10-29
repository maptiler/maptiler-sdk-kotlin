/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.net.Uri
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.URL

class MTOfflineEstimatorTest {
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

    private val base = "https://estimator.test"

    // A style with one vector source (TileJSON, zoom 0-14), one raster source with inline tiles,
    // two font stacks and one sprite.
    private val responses =
        mapOf(
            "$base/style.json" to
                """
                {
                  "version": 8,
                  "sprite": "$base/sprite",
                  "glyphs": "$base/fonts/{fontstack}/{range}.pbf",
                  "sources": {
                    "planet": { "type": "vector", "url": "$base/planet/tiles.json" },
                    "hillshade": { "type": "raster", "tiles": ["$base/hillshade/{z}/{x}/{y}.png"], "minzoom": 2, "maxzoom": 12 }
                  },
                  "layers": [
                    { "id": "a", "type": "symbol", "source": "planet", "layout": { "text-font": ["Noto Sans Regular"] } },
                    { "id": "b", "type": "symbol", "source": "planet", "layout": { "text-font": ["Noto Sans Bold"] } }
                  ]
                }
                """.trimIndent(),
            "$base/planet/tiles.json" to """{ "tiles": ["$base/planet/{z}/{x}/{y}.pbf"], "minzoom": 0, "maxzoom": 14 }""",
        )

    private val requests = mutableListOf<String>()

    private fun resolver() =
        MTOfflineMetadataResolver(fetch = { url ->
            synchronized(requests) { requests.add(url) }
            responses[url] ?: throw MTOfflineError.BadResponse(404)
        })

    private fun region(
        minZoom: Int = 0,
        maxZoom: Int = 16,
    ) = MTOfflineRegionDefinition(
        bbox = MTBoundingBox(8.52, 47.36, 8.56, 47.39),
        minZoom = minZoom,
        maxZoom = maxZoom,
        referenceStyle = MTMapReferenceStyle.CUSTOM(URL("$base/style.json")),
    )

    private val vector = MTOfflineSourceMetadata("v", "vector", "https://t/{z}/{x}/{y}.pbf", 0, 14)

    @Test
    fun testEstimateMatchesManifestWithoutTileRequests() =
        runBlocking {
            val resolver = resolver()
            val stats = MTOfflineEstimator(resolver).estimatePack(region())

            assertEquals(listOf("$base/style.json", "$base/planet/tiles.json"), requests)

            // Vector tiles stop at the TileJSON max zoom, raster tiles at the style's source max zoom.
            val planet = stats.sources.first { it.id == "planet" }
            assertEquals(0..14, planet.minZoom..planet.maxZoom)
            val hillshade = stats.sources.first { it.id == "hillshade" }
            assertEquals(2..12, hillshade.minZoom..hillshade.maxZoom)

            assertEquals(2 * MTOfflineGlyphRanges.LATIN_GREEK_CYRILLIC_STARTS.size, stats.glyphCount)
            assertEquals(4, stats.spriteCount)
            assertEquals(1, stats.styleCount)
            assertEquals(stats.tileCount + stats.glyphCount + stats.spriteCount + 1, stats.totalResourceCount)

            val manifest = MTLocalPlanner(resolver).generateManifest(region())
            assertEquals(2, requests.size) // the planner reused the cached metadata
            val manifestCount = manifest.tiles.size + manifest.glyphs.size + manifest.sprites.size + 1
            assertEquals(stats.resourceCount, manifestCount)
            assertEquals(stats.tileCount, manifest.tiles.size.toLong())
        }

    @Test
    fun testEstimateWithMetadataIsOffline() =
        runBlocking {
            val estimator = MTOfflineEstimator(resolver())
            val metadata = estimator.styleMetadata(region())
            val requestsAfterResolve = requests.size

            val json = JsonConfig.json.encodeToString(MTOfflineStyleMetadata.serializer(), metadata)
            assertTrue(json.contains("\"tileURLTemplate\""))
            val decoded = JsonConfig.json.decodeFromString(MTOfflineStyleMetadata.serializer(), json)
            assertEquals(metadata, decoded)

            val online = estimator.estimatePack(region())
            val offline = MTOfflineEstimator().estimatePack(region(), decoded)
            assertEquals(online, offline)
            assertEquals(requestsAfterResolve, requests.size)
        }

    @Test
    fun testRedactedMetadata() {
        val metadata =
            MTOfflineStyleMetadata(
                styleUrl = "https://t/maps/streets/style.json?key=SECRET",
                sources = listOf(vector.copy(tileUrlTemplate = "https://t/tiles/{z}/{x}/{y}.pbf?key=SECRET&mtsid=SESSION")),
                terrainSource =
                    MTOfflineSourceMetadata(
                        "terrain",
                        "raster-dem",
                        "https://t/terrain/{z}/{x}/{y}.webp?mtsid=SESSION&key=SECRET&v=2",
                        0,
                        12,
                    ),
                glyphsTemplate = "https://t/fonts/{fontstack}/{range}.pbf?key=SECRET",
                fontStacks = listOf(listOf("Noto Sans Regular")),
                sprites = listOf(MTOfflineSpriteMetadata("default", "https://t/maps/streets/sprite?key=SECRET")),
            )

        val redacted = metadata.redacted()
        assertEquals("https://t/maps/streets/style.json", redacted.styleUrl)
        assertEquals("https://t/tiles/{z}/{x}/{y}.pbf", redacted.sources.single().tileUrlTemplate)
        assertEquals("https://t/terrain/{z}/{x}/{y}.webp?v=2", redacted.terrainSource?.tileUrlTemplate)
        assertEquals("https://t/fonts/{fontstack}/{range}.pbf", redacted.glyphsTemplate)
        assertEquals("https://t/maps/streets/sprite", redacted.sprites.single().url)

        val json = JsonConfig.json.encodeToString(MTOfflineStyleMetadata.serializer(), redacted)
        assertTrue(!json.contains("SECRET") && !json.contains("SESSION"))

        val definition =
            MTOfflineRegionDefinition(
                bbox = MTBoundingBox(8.53, 47.37, 8.55, 47.38),
                minZoom = 0,
                maxZoom = 14,
                referenceStyle = MTMapReferenceStyle.STREETS,
                isTerrainEnabled = true,
            )
        val estimator = MTOfflineEstimator()
        assertEquals(estimator.estimatePack(definition, metadata), estimator.estimatePack(definition, redacted))
    }

    @Test
    fun testOverzoom() {
        val metadata = MTOfflineStyleMetadata(sources = listOf(vector))
        val definition =
            MTOfflineRegionDefinition(
                bbox = MTBoundingBox(8.53, 47.37, 8.55, 47.38),
                minZoom = 16,
                maxZoom = 18,
                referenceStyle = MTMapReferenceStyle.STREETS,
            )
        val source = MTOfflineEstimator().estimatePack(definition, metadata).sources.single()
        assertEquals(14, source.minZoom)
        assertEquals(14, source.maxZoom)
        assertEquals(setOf(14), source.tilesPerZoom.keys)
    }

    @Test
    fun testTerrain() {
        val terrain = MTOfflineSourceMetadata("maptiler-terrain", "raster-dem", "https://t/{z}/{x}/{y}.webp", 0, 12)
        val definition =
            MTOfflineRegionDefinition(
                bbox = MTBoundingBox(8.52, 47.36, 8.56, 47.39),
                minZoom = 0,
                maxZoom = 14,
                referenceStyle = MTMapReferenceStyle.STREETS,
                isTerrainEnabled = true,
            )

        val withTerrain = MTOfflineStyleMetadata(sources = listOf(vector), terrainSource = terrain)
        assertEquals(listOf("v", "maptiler-terrain"), MTOfflineEstimator().estimatePack(definition, withTerrain).sources.map { it.id })

        val styleTerrain = MTOfflineSourceMetadata("dem", "raster-dem", "https://t/{z}/{x}/{y}.webp", 0, 12)
        val builtIn = MTOfflineStyleMetadata(sources = listOf(vector, styleTerrain), terrainSource = terrain)
        assertEquals(listOf("v", "dem"), MTOfflineEstimator().estimatePack(definition, builtIn).sources.map { it.id })

        assertThrows(MTOfflineError.MissingKey::class.java) {
            MTOfflineEstimator().estimatePack(definition, MTOfflineStyleMetadata(sources = listOf(vector)))
        }
    }

    @Test
    fun testLimitAndPackCount() {
        val metadata =
            MTOfflineStyleMetadata(
                sources = listOf(vector),
                glyphsTemplate = "https://t/{fontstack}/{range}.pbf",
                fontStacks = listOf(listOf("A")),
                sprites = listOf(MTOfflineSpriteMetadata("default", "https://t/sprite")),
            )
        val bbox = MTBoundingBox(8.52, 47.36, 8.56, 47.39)
        val definition =
            MTOfflineRegionDefinition(
                bbox,
                0,
                14,
                MTMapReferenceStyle.STREETS,
                maxTileCount = 500,
                glyphRanges = MTOfflineGlyphRanges.All,
            )
        val stats = MTOfflineEstimator().estimatePack(definition, metadata)

        assertEquals(500, stats.resourceLimit)
        assertEquals(stats.totalResourceCount > 500, stats.exceedsLimit)
        // 256 glyphs + 4 sprite files + 1 style leave 239 tiles per pack.
        val tileBudget = 500L - 261
        assertEquals((stats.tileCount + tileBudget - 1) / tileBudget, stats.minimumPackCount)

        val tight =
            MTOfflineRegionDefinition(
                bbox,
                0,
                14,
                MTMapReferenceStyle.STREETS,
                maxTileCount = 200,
                glyphRanges = MTOfflineGlyphRanges.All,
            )
        assertNull(MTOfflineEstimator().estimatePack(tight, metadata).minimumPackCount)
    }

    @Test
    fun testInvalidRegion() {
        val definition =
            MTOfflineRegionDefinition(MTBoundingBox(0.0, 0.0, 1.0, 1.0), 10, 5, MTMapReferenceStyle.STREETS)
        assertThrows(MTOfflineError.InvalidRegion::class.java) {
            MTOfflineEstimator().estimatePack(definition, MTOfflineStyleMetadata(sources = emptyList()))
        }
    }

    @Test
    fun testLimitExceededByPlanner() =
        runBlocking {
            val definition =
                MTOfflineRegionDefinition(
                    bbox = MTBoundingBox(-180.0, -85.0, 180.0, 85.0),
                    minZoom = 0,
                    maxZoom = 14,
                    referenceStyle = MTMapReferenceStyle.CUSTOM(URL("$base/style.json")),
                    maxTileCount = 10,
                )
            val error =
                runCatching { MTLocalPlanner(resolver()).estimate(definition) }.exceptionOrNull()
            assertTrue(error is MTOfflineError.ExceedsMaximumTileCount)
            assertEquals(10, (error as MTOfflineError.ExceedsMaximumTileCount).limit)
        }
}
