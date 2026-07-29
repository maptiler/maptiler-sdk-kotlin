package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointMultByPointTest {
    @Test
    fun `toJS returns proper string`() {
        val point1 = MTPoint(14.4, 50.0)
        val point2 = MTPoint(2.0, 3.0)
        val command = PointMultByPoint(point1, point2)
        assertEquals(
            "(() => { const p = new maptilersdk.Point(14.4, 50.0)" +
                ".multByPoint(new maptilersdk.Point(2.0, 3.0)); return { x: p.x, y: p.y }; })();",
            command.toJS(),
        )
    }
}
