package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.LngLat
import org.junit.Assert.assertEquals
import org.junit.Test

class LngLatWrapTest {
    @Test
    fun `toJS returns proper string`() {
        val lngLat = LngLat(200.0, 50.08170)
        val command = LngLatWrap(lngLat)
        assertEquals("new maptilersdk.LngLat(200.0, 50.0817).wrap();", command.toJS())
    }
}
