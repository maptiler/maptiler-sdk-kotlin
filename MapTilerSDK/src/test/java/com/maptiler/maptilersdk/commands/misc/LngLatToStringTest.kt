package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.LngLat
import org.junit.Assert.assertEquals
import org.junit.Test

class LngLatToStringTest {
    @Test
    fun `toJS returns proper string`() {
        val lngLat = LngLat(14.41790, 50.08170)
        val command = LngLatToString(lngLat)
        assertEquals("new maptilersdk.LngLat(14.4179, 50.0817).toString();", command.toJS())
    }
}
