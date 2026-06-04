package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointAngleWithTest {
    @Test
    fun `toJS returns proper string`() {
        val point1 = MTPoint(14.4, 50.0)
        val point2 = MTPoint(1.0, 2.0)
        val command = PointAngleWith(point1, point2)
        assertEquals(
            "new maptilersdk.Point(14.4, 50.0).angleWith(new maptilersdk.Point(1.0, 2.0));",
            command.toJS(),
        )
    }
}
