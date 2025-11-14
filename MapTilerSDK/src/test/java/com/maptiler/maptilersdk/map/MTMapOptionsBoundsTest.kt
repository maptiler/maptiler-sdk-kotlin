package com.maptiler.maptilersdk.map

import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.types.MTBounds
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Test

class MTMapOptionsBoundsTest {
    @Test
    fun `bounds properties serialize with map options`() {
        val bounds = MTBounds(-10.0, -5.0, 10.0, 5.0)
        val options = MTMapOptions(bounds = bounds, maxBounds = bounds)

        val jsonElement = JsonConfig.json.parseToJsonElement(JsonConfig.json.encodeToString(options)).jsonObject

        assertEquals("[[-10.0,-5.0],[10.0,5.0]]", jsonElement["bounds"].toString())
        assertEquals("[[-10.0,-5.0],[10.0,5.0]]", jsonElement["maxBounds"].toString())
    }
}
