/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class MTOfflineSerializationTest {
    @Test
    fun testMetadataSerialization() {
        val region =
            MTOfflineRegionDefinition(
                bbox = MTBoundingBox(0.0, 0.0, 10.0, 10.0),
                minZoom = 0,
                maxZoom = 10,
                referenceStyle = MTMapReferenceStyle.STREETS,
            )
        val createdAt = Instant.parse("2026-06-10T10:00:00Z")
        val expiresAt = createdAt.plusSeconds(3600)
        val context = "test-context".toByteArray()

        val metadata =
            MTOfflinePackMetadata(
                id = "test-id",
                state = MTOfflinePackState.PENDING,
                size = 100,
                createdAt = createdAt,
                expiresAt = expiresAt,
                context = context,
                region = region,
                totalResources = 10,
                totalTileResources = 5,
                downloadedResources = 2,
            )

        val json = metadata.toJson()
        val decoded = MTOfflinePackMetadata.fromJson(json)

        assertEquals(metadata.id, decoded.id)
        assertEquals(metadata.state, decoded.state)
        assertEquals(metadata.size, decoded.size)
        assertEquals(metadata.createdAt, decoded.createdAt)
        assertEquals(metadata.expiresAt, decoded.expiresAt)
        assertArrayEquals(metadata.context, decoded.context)
        assertEquals(metadata.region.minZoom, decoded.region.minZoom)
        assertEquals(metadata.totalResources, decoded.totalResources)
    }

    @Test
    fun testMetadataExpiresAtFallback() {
        val json =
            """
            {
                "id": "test-id",
                "state": "PENDING",
                "size": 0,
                "createdAt": "2026-06-10T10:00:00Z",
                "region": {
                    "geometry": {
                        "boundingBox": {
                            "minLon": 0.0,
                            "minLat": 0.0,
                            "maxLon": 10.0,
                            "maxLat": 10.0
                        }
                    },
                    "minZoom": 0,
                    "maxZoom": 10,
                    "referenceStyle": "STREETS",
                    "pixelRatio": 1.0
                }
            }
            """.trimIndent()

        val decoded = MTOfflinePackMetadata.fromJson(json)
        val expectedExpiresAt =
            Instant.parse("2026-06-10T10:00:00Z")
                .plusMillis(MTOfflineConfiguration.DEFAULT_EXPIRATION_INTERVAL)

        assertEquals(expectedExpiresAt, decoded.expiresAt)
    }

    @Test
    fun testLegacyBboxSupport() {
        val json =
            """
            {
                "id": "test-id",
                "state": "PENDING",
                "size": 0,
                "createdAt": "2026-06-10T10:00:00Z",
                "region": {
                    "bbox": {
                        "minLon": 10.0,
                        "minLat": 20.0,
                        "maxLon": 30.0,
                        "maxLat": 40.0
                    },
                    "minZoom": 0,
                    "maxZoom": 10,
                    "referenceStyle": "STREETS",
                    "pixelRatio": 1.0
                }
            }
            """.trimIndent()

        val decoded = MTOfflinePackMetadata.fromJson(json)
        val geometry = decoded.region.geometry
        assert(geometry is MTOfflineRegionGeometry.BoundingBox)
        val bbox = (geometry as MTOfflineRegionGeometry.BoundingBox).bbox
        assertEquals(10.0, bbox.minLon, 0.0001)
        assertEquals(40.0, bbox.maxLat, 0.0001)
    }

    @Test
    fun testRouteSerialization() {
        val coords = listOf(LngLat(0.0, 0.0), LngLat(1.0, 1.0))
        val region =
            MTOfflineRegionDefinition(
                geometry = MTOfflineRegionGeometry.Route(coords),
                minZoom = 0,
                maxZoom = 10,
                referenceStyle = MTMapReferenceStyle.STREETS,
            )

        val json =
            MTOfflineRegionDefinition.serializer().let {
                com.maptiler.maptilersdk.helpers.JsonConfig.json.encodeToString(it, region)
            }

        // Verify format: {"geometry": {"route": [[0.0, 0.0], [1.0, 1.0]]}, "bbox": {...}, ...}
        assert(json.contains("\"route\":[[0.0,0.0],[1.0,1.0]]"))
        assert(json.contains("\"bbox\":{\"minLon\":0.0,\"minLat\":0.0,\"maxLon\":1.0,\"maxLat\":1.0}"))

        val decoded =
            com.maptiler.maptilersdk.helpers.JsonConfig.json.decodeFromString(
                MTOfflineRegionDefinition.serializer(),
                json,
            )
        assert(decoded.geometry is MTOfflineRegionGeometry.Route)
        assertEquals(2, (decoded.geometry as MTOfflineRegionGeometry.Route).coordinates.size)
    }

    @Test
    fun testManifestSerialization() {
        val metadata =
            MTManifestMetadata(
                referenceStyle = MTMapReferenceStyle.STREETS,
                bbox = MTBoundingBox(0.0, 0.0, 10.0, 10.0),
                minZoom = 0,
                maxZoom = 10,
                pixelRatio = 1.0f,
            )
        val manifest =
            MTManifest(
                metadata = metadata,
                tiles = listOf(MTMapResource(url = "http://example.com/1.png", destinationPath = "1.png")),
            )

        val json = manifest.toJson()
        val decoded = MTManifest.fromJson(json)

        assertEquals(manifest.version, decoded.version)
        assertEquals(manifest.metadata.referenceStyle, decoded.metadata.referenceStyle)
        assertEquals(1, decoded.tiles.size)
        assertEquals("http://example.com/1.png", decoded.tiles[0].url)
    }
}
