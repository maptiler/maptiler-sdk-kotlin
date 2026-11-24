package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.map.LngLat
import org.junit.Assert.assertEquals
import org.junit.Test

class QueryTerrainElevationTest {
    @Test
    fun `toJS generates correct script`() {
        val command =
            QueryTerrainElevation(
                coordinates = LngLat(10.123456, 20.654321),
            )
        val js = command.toJS()
        assertEquals(
            "map.queryTerrainElevation([10.123456, 20.654321]);",
            js,
        )
    }
}
