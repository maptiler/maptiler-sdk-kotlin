package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.map.types.MTPoint
import com.maptiler.maptilersdk.offline.MTBoundingBox
import org.junit.Assert.assertEquals
import org.junit.Test

class QueryRenderedFeaturesTest {
    @Test
    fun `toJS generates correct script for point`() {
        val command =
            QueryRenderedFeatures(
                point = MTPoint(10.5, 20.123456),
                layers = listOf("layer1", "layer2"),
                filter = null,
            )
        val js = command.toJS()
        assertEquals(
            "JSON.stringify(map.queryRenderedFeatures([10.500000, 20.123456], {\"layers\":[\"layer1\",\"layer2\"]}));",
            js,
        )
    }

    @Test
    fun `toJS generates correct script for bounding box`() {
        val command =
            QueryRenderedFeatures(
                bbox = MTBoundingBox(10.0, 20.0, 30.0, 40.0),
                layers = null,
                filter = "[\"==\",\"class\",\"street\"]",
            )
        val js = command.toJS()
        assertEquals(
            "JSON.stringify(map.queryRenderedFeatures(" +
                "[map.project([10.000000, 20.000000]), " +
                "map.project([30.000000, 40.000000])], " +
                "{\"filter\":\"[\\\"==\\\",\\\"class\\\",\\\"street\\\"]\"}));",
            js,
        )
    }
}
