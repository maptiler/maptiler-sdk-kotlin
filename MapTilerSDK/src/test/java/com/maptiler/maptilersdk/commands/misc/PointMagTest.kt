package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointMagTest {
    @Test
    fun `toJS returns proper string`() {
        val point = MTPoint(3.0, 4.0)
        val command = PointMag(point)
        assertEquals(
            "new maptilersdk.Point(3.0, 4.0).mag();",
            command.toJS(),
        )
    }
}
