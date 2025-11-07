/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.style.AddSource
import com.maptiler.maptilersdk.map.style.source.MTGeoJSONSource
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.net.URL

class GeoJSONSourceTests {
    @Test fun addSource_GeoJSON_WithHttpUrl_EncodesDataAndNoId() {
        val src = MTGeoJSONSource("g-http", URL("https://example.com/data.geojson"))

        val js = AddSource(src).toJS()

        assertTrue(js.startsWith("${MTBridge.MAP_OBJECT}.addSource('g-http', {"))
        assertTrue(js.contains("\"type\":\"geojson\""))
        assertTrue(js.contains("\"data\":\"https://example.com/data.geojson\""))
        assertFalse(js.contains("\"id\":"))
        assertFalse(js.contains("jsonString"))
    }

    @Test fun addSource_GeoJSON_WithInlineJson_EmbedsObjectAndNoEscapes() {
        val inline = """{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[0.0,0.0]}}]}"""
        val src =
            MTGeoJSONSource("g-inline", inline).apply {
                isCluster = true
                clusterRadius = 40.0
            }

        val js = AddSource(src).toJS()

        assertTrue(js.startsWith("${MTBridge.MAP_OBJECT}.addSource('g-inline', {"))
        // data should be an object, not an escaped string
        assertTrue(js.contains("\"data\":{\"type\":\"FeatureCollection\""))
        assertFalse(js.contains("\\\"type\\\""))
        // cluster options included when enabled
        assertTrue(js.contains("\"cluster\":true"))
        assertTrue(js.contains("\"clusterRadius\":40.0"))
        // id must never appear inside the source object
        assertFalse(js.contains("\"id\":"))
    }

    @Test fun addSource_GeoJSON_WithFileUrl_KeepsQuotedString() {
        val src = MTGeoJSONSource("g-file", URL("file:///sdcard/Download/data.geojson"))

        val js = AddSource(src).toJS()

        assertTrue(js.contains("\"data\":\"file:///sdcard/Download/data.geojson\""))
    }
}
