/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import android.graphics.Bitmap
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.style.AddSource
import com.maptiler.maptilersdk.commands.style.SetDataToSource
import com.maptiler.maptilersdk.commands.style.SetTilesToSource
import com.maptiler.maptilersdk.helpers.ImageHelper
import com.maptiler.maptilersdk.map.style.layer.symbol.MTSymbolLayer
import com.maptiler.maptilersdk.map.style.source.MTGeoJSONSource
import com.maptiler.maptilersdk.map.style.source.MTVectorTileSource
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URL

class StyleAndCommandsTests {
    @Test fun setTilesToSourceToJS_ReturnsValidJSString() {
        val tiles =
            arrayOf(
                URL("https://example.com/t/1"),
                URL("https://example.com/t/2"),
            )
        val source = MTVectorTileSource("v-src", URL("https://example.com/placeholder"))

        val js = SetTilesToSource(tiles, source).toJS()
        assertEquals(
            "${MTBridge.MAP_OBJECT}.getSource('v-src').setTiles([\"https://example.com/t/1\",\"https://example.com/t/2\"]);",
            js,
        )
    }

    @Test fun setDataToSourceToJS_ReturnsValidJSString() {
        val dataUrl = URL("https://example.com/data.geojson")
        val source = MTGeoJSONSource("g-src", dataUrl)

        val js = SetDataToSource(dataUrl, source).toJS()
        assertEquals(
            "${MTBridge.MAP_OBJECT}.getSource('g-src').setData('https://example.com/data.geojson');",
            js,
        )
    }

    @Test fun addSource_Vector_WithUrlOrTiles_ReturnsExpectedFields() {
        // With URL
        run {
            val srcUrl = MTVectorTileSource("v1", URL("https://tiles.example.com/tile.json"))
            val js = AddSource(srcUrl).toJS()
            assertTrue(js.contains("type: 'vector'"))
            assertTrue(js.contains("url: 'https://tiles.example.com/tile.json'"))
            assertTrue(!js.contains("tiles:"))
        }

        // With tiles
        run {
            val srcTiles = MTVectorTileSource("v2", URL("https://placeholder.example.com"))
            srcTiles.tiles = arrayOf(URL("https://tiles.example.com/a/{z}/{x}/{y}.pbf"))
            srcTiles.url = null
            val js = AddSource(srcTiles).toJS()
            assertTrue(js.contains("type: 'vector'"))
            assertTrue(js.contains("tiles: [\"https://tiles.example.com/a/{z}/{x}/{y}.pbf\"]"))
            assertTrue(!js.contains("url:"))
        }
    }

    @Test fun addLayer_Symbol_NoIcon_AddsLayerDirectly() {
        val layer = MTSymbolLayer("sym1", "src1")
        val js = com.maptiler.maptilersdk.commands.style.AddLayer(layer).toJS()
        assertEquals("${MTBridge.MAP_OBJECT}.addLayer({\"id\":\"sym1\",\"type\":\"symbol\",\"source\":\"src1\"});", js)
    }

    @Test fun addLayer_Symbol_WithIcon_AddsImageAndLayer() {
        // Mock bitmap to avoid Android framework dependency in unit tests
        val bmp = mockk<Bitmap>()
        every { bmp.hasAlpha() } returns true
        every { bmp.compress(any(), any(), any()) } returns true

        mockkObject(ImageHelper)
        every { ImageHelper.encodeImage(any()) } returns "AAA"

        val layer = MTSymbolLayer("sym2", "src2", bmp)
        val js = com.maptiler.maptilersdk.commands.style.AddLayer(layer).toJS()

        // Check presence of image add and addLayer call
        assertTrue(js.contains("addImage('iconsym2'"))
        assertTrue(js.contains("${MTBridge.MAP_OBJECT}.addLayer({"))
    }
}
