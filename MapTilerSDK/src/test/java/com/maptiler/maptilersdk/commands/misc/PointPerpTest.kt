package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointPerpTest {
    @Test
    fun `toJS returns proper string`() {
        val point = MTPoint(1.0, 2.0)
        val command = PointPerp(point)
        assertEquals(
            "(() => { const p = new maptilersdk.Point(1.0, 2.0).perp(); return { x: p.x, y: p.y }; })();",
            command.toJS(),
        )
    }
}
