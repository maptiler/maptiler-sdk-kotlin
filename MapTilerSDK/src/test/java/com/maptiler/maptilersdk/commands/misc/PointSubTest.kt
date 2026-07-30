package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointSubTest {
    @Test
    fun `toJS returns proper string`() {
        val point1 = MTPoint(14.4, 50.0)
        val point2 = MTPoint(1.0, 2.0)
        val command = PointSub(point1, point2)
        assertEquals(
            "(() => { const p = new maptilersdk.Point(14.4, 50.0).sub(new maptilersdk.Point(1.0, 2.0)); return { x: p.x, y: p.y }; })();",
            command.toJS(),
        )
    }
}
