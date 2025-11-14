package com.maptiler.maptilersdk.map.types

import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.LngLat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Test

class MTBoundsSerializerTest {
    @Test
    fun `serialize outputs array of coordinate pairs`() {
        val bounds = MTBounds(-10.0, -5.0, 10.0, 5.0)

        val serialized = JsonConfig.json.encodeToString(bounds)

        assertEquals("[[-10.0,-5.0],[10.0,5.0]]", serialized)
    }

    @Test
    fun `deserialize supports maplibre object shape`() {
        val json = """{"_sw":{"lng":-10.0,"lat":-5.0},"_ne":{"lng":10.0,"lat":5.0}}"""

        val bounds = JsonConfig.json.decodeFromString<MTBounds>(json)

        assertEquals(LngLat(-10.0, -5.0), bounds.southwest)
        assertEquals(LngLat(10.0, 5.0), bounds.northeast)
    }

    @Test
    fun `deserialize supports flat array`() {
        val json = "[-10.0,-5.0,10.0,5.0]"

        val bounds = JsonConfig.json.decodeFromString<MTBounds>(json)

        assertEquals(LngLat(-10.0, -5.0), bounds.southwest)
        assertEquals(LngLat(10.0, 5.0), bounds.northeast)
    }
}
