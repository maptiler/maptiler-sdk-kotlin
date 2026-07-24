package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointDistSqrTest {
    @Test
    fun `toJS returns proper string`() {
        val point1 = MTPoint(14.4, 50.0)
        val point2 = MTPoint(1.0, 2.0)
        val command = PointDistSqr(point1, point2)
        assertEquals(
            "new maptilersdk.Point(14.4, 50.0).distSqr(new maptilersdk.Point(1.0, 2.0));",
            command.toJS(),
        )
    }
}
