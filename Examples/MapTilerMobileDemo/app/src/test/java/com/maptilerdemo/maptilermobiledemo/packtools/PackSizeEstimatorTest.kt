/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo.packtools

import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.offline.MTBoundingBox
import com.maptiler.maptilersdk.offline.MTOfflineEstimator
import com.maptiler.maptilersdk.offline.MTOfflineRegionDefinition
import com.maptiler.maptilersdk.offline.MTOfflineRegionGeometry
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

/**
 * The Android estimator must reproduce the size estimates recorded from the iOS demo app
 * (maptiler-offline-estimator/fixtures/size-estimate-fixtures.json), using the bundled assets.
 */
class PackSizeEstimatorTest {
    private val assets = File("src/main/assets/packtools")

    private fun resource(name: String) =
        requireNotNull(javaClass.getResourceAsStream("/packtools/$name")).bufferedReader().use { it.readText() }

    @Test
    fun estimatesMatchTheIosDemoApp() {
        val snapshot = packToolsJson.decodeFromString<PackSnapshot>(File(assets, "snapshot.json").readText())
        val model = packToolsJson.decodeFromString<PackSizeModel>(File(assets, "size-model.json").readText())
        val population = PopulationGrid.fromBytes(model.population!!.zoom, File(assets, model.population!!.file).readBytes())
        val cases = packToolsJson.parseToJsonElement(resource("size-estimate-fixtures.json")).jsonObject["cases"]!!.jsonArray

        for (case in cases) {
            val fixture = case.jsonObject
            val name = fixture["name"]!!.jsonPrimitive.content
            val request = fixture["request"]!!.jsonObject
            val expected = fixture["expected"]!!.jsonObject.mapValues { it.value.jsonPrimitive.double }

            val metadata = snapshot.styles.getValue(request["styleKey"]!!.jsonPrimitive.content)
            val region = region(request)
            val stats = MTOfflineEstimator().estimatePack(region, metadata)
            val size = PackSizeEstimator.estimate(region, metadata, stats, model, population)

            assertEquals(name, expected.getValue("resourceCount"), stats.totalResourceCount.toDouble(), 0.0)
            assertEquals(name, expected.getValue("tileCount"), stats.tileCount.toDouble(), 0.0)
            assertEquals(name, expected.getValue("tilesBytes"), size.tiles.bytes, 1.0)
            assertEquals(name, expected.getValue("tilesAllocated"), size.tiles.allocated, 1.0)
            assertEquals(name, expected.getValue("overheadAllocated"), size.overhead.allocated, 1.0)
            assertEquals(name, expected.getValue("totalAllocated"), size.total.allocated, 1.0)
            assertEquals(name, expected.getValue("tileError"), size.tileError, 1e-9)
        }
    }

    // Decodes the Swift-encoded geometry of a fixture request ({"polygon":{"_0":[[lon,lat],...]}}).
    private fun region(request: JsonObject): MTOfflineRegionDefinition {
        val geometry =
            request["geometry"]?.jsonPrimitive?.content?.let { encoded ->
                val decoded = packToolsJson.parseToJsonElement(encoded).jsonObject
                val (type, value) = decoded.entries.first()
                val points = value.jsonObject["_0"]!!.jsonArray.map { LngLat(it.jsonArray[0].jsonPrimitive.double, it.jsonArray[1].jsonPrimitive.double) }
                if (type == "polygon") MTOfflineRegionGeometry.Polygon(points) else MTOfflineRegionGeometry.Route(points)
            } ?: request["bbox"]!!.jsonArray.map { it.jsonPrimitive.double }.let {
                MTOfflineRegionGeometry.BoundingBox(MTBoundingBox(it[0], it[1], it[2], it[3]))
            }
        val style = PackStyle.withId(request["styleKey"]!!.jsonPrimitive.content)!!
        return MTOfflineRegionDefinition(
            geometry = geometry,
            minZoom = request["minZoom"]!!.jsonPrimitive.int,
            maxZoom = request["maxZoom"]!!.jsonPrimitive.int,
            referenceStyle = style.style,
            styleVariant = style.variant,
            padding = request["padding"]?.jsonPrimitive?.doubleOrNull,
            isTerrainEnabled = request["terrain"]?.jsonPrimitive?.booleanOrNull ?: false,
        )
    }
}
