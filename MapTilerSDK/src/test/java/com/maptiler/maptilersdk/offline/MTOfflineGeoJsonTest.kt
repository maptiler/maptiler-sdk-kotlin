/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import org.junit.Assert.assertEquals
import org.junit.Test

class MTOfflineGeoJsonTest {
    @Test
    fun testRouteFromLineString() {
        val json =
            """
            {
                "type": "LineString",
                "coordinates": [[100.0, 0.0], [101.0, 1.0]]
            }
            """.trimIndent()
        val route = MTOfflineRegionGeometry.Route.fromGeoJson(json)
        assertEquals(2, route.coordinates.size)
        assertEquals(100.0, route.coordinates[0].lng, 0.0001)
        assertEquals(0.0, route.coordinates[0].lat, 0.0001)
        assertEquals(101.0, route.coordinates[1].lng, 0.0001)
        assertEquals(1.0, route.coordinates[1].lat, 0.0001)
    }

    @Test
    fun testRouteFromFeature() {
        val json =
            """
            {
                "type": "Feature",
                "geometry": {
                    "type": "LineString",
                    "coordinates": [[10.0, 20.0], [30.0, 40.0]]
                },
                "properties": {}
            }
            """.trimIndent()
        val route = MTOfflineRegionGeometry.Route.fromGeoJson(json)
        assertEquals(2, route.coordinates.size)
        assertEquals(10.0, route.coordinates[0].lng, 0.0001)
        assertEquals(20.0, route.coordinates[0].lat, 0.0001)
    }

    @Test
    fun testRouteFromFeatureCollection() {
        val json =
            """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "type": "Feature",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [1.0, 2.0]
                        }
                    },
                    {
                        "type": "Feature",
                        "geometry": {
                            "type": "LineString",
                            "coordinates": [[10.0, 20.0], [30.0, 40.0]]
                        }
                    }
                ]
            }
            """.trimIndent()
        val route = MTOfflineRegionGeometry.Route.fromGeoJson(json)
        assertEquals(2, route.coordinates.size)
        assertEquals(10.0, route.coordinates[0].lng, 0.0001)
    }

    @Test
    fun testPolygonFromGeoJson() {
        val json =
            """
            {
                "type": "Polygon",
                "coordinates": [
                    [[0.0, 0.0], [10.0, 0.0], [10.0, 10.0], [0.0, 10.0], [0.0, 0.0]]
                ]
            }
            """.trimIndent()
        val polygon = MTOfflineRegionGeometry.Polygon.fromGeoJson(json)
        assertEquals(5, polygon.coordinates.size)
        assertEquals(0.0, polygon.coordinates[0].lng, 0.0001)
        assertEquals(0.0, polygon.coordinates[0].lat, 0.0001)
        assertEquals(10.0, polygon.coordinates[2].lng, 0.0001)
        assertEquals(10.0, polygon.coordinates[2].lat, 0.0001)
    }
}
