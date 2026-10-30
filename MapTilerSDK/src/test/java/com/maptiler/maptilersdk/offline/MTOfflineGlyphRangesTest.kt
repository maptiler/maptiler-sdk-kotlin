/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
import android.net.Uri
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MTOfflineGlyphRangesTest {
    private val metadata =
        MTOfflineStyleMetadata(
            sources = listOf(MTOfflineSourceMetadata("v", "vector", "https://t/{z}/{x}/{y}.pbf", 0, 14)),
            glyphsTemplate = "https://t/{fontstack}/{range}.pbf",
            fontStacks = listOf(listOf("A"), listOf("B", "C")),
        )

    private fun region(glyphRanges: MTOfflineGlyphRanges = MTOfflineGlyphRanges.LatinGreekCyrillic) =
        MTOfflineRegionDefinition(
            bbox = MTBoundingBox(8.52, 47.36, 8.56, 47.39),
            minZoom = 0,
            maxZoom = 10,
            referenceStyle = MTMapReferenceStyle.STREETS,
            glyphRanges = glyphRanges,
        )

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

    @Test
    fun testDefaultRanges() {
        val definition =
            MTOfflineRegionDefinition(MTBoundingBox(8.52, 47.36, 8.56, 47.39), 0, 10, MTMapReferenceStyle.STREETS)
        assertEquals(MTOfflineGlyphRanges.LatinGreekCyrillic, definition.glyphRanges)

        val plan = MTOfflineResourcePlan(definition, metadata)
        val paths = plan.glyphResources().map { it.destinationPath }
        assertEquals(2 * 11, paths.size)
        assertEquals(paths.size, plan.glyphCount)
        assertTrue(paths.contains("glyphs/A/1024-1279.pbf"))
        assertFalse(paths.contains("glyphs/A/19968-20223.pbf"))
    }

    @Test
    fun testAllAndCustomRanges() {
        assertEquals(2 * 256, MTOfflineResourcePlan(region(MTOfflineGlyphRanges.All), metadata).glyphCount)

        val custom = MTOfflineGlyphRanges.Custom(listOf(0, 300, 0x4E00, 0x4E10, -5, 70_000))
        assertEquals(listOf(0, 256, 0x4E00), custom.rangeStarts)
        val plan = MTOfflineResourcePlan(region(custom), metadata)
        assertTrue(plan.glyphResources().map { it.destinationPath }.contains("glyphs/B,C/19968-20223.pbf"))
        assertEquals(6, plan.glyphCount)
    }

    @Test
    fun testDecodingLegacyDefinition() {
        val json = JsonConfig.json.encodeToJsonElement(MTOfflineRegionDefinition.serializer(), region())
        val legacy = JsonObject(json.jsonObject - "glyphRanges")
        val decoded = JsonConfig.json.decodeFromJsonElement(MTOfflineRegionDefinition.serializer(), legacy)
        assertEquals(MTOfflineGlyphRanges.All, decoded.glyphRanges)

        val custom = region(MTOfflineGlyphRanges.Custom(listOf(0x4E00)))
        val roundTrip =
            JsonConfig.json.decodeFromString(
                MTOfflineRegionDefinition.serializer(),
                JsonConfig.json.encodeToString(MTOfflineRegionDefinition.serializer(), custom),
            )
        assertEquals(custom, roundTrip)
    }

    @Test
    fun testMissingGlyphRangeResolvesToEmptyResponse() {
        val context = mockk<Context>(relaxed = true)
        every { context.filesDir } returns java.nio.file.Files.createTempDirectory("router").toFile()
        val router = MTOfflineRouter(context)
        val glyphs = router.resolve("/offline/pack/glyphs/Roboto%20Regular,Noto%20Sans%20Regular/19968-20223.pbf")
        assertEquals(MTOfflineRouter.ResolvedResource(null, "application/x-protobuf"), glyphs)
        assertNull(router.resolve("/offline/pack/tiles/v/1/2/3.pbf"))
        assertNull(router.resolve("/other/pack/glyphs/A/0-255.pbf"))
    }
}
