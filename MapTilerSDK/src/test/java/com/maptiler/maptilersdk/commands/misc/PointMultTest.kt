package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointMultTest {
    @Test
    fun `toJS returns proper string`() {
        val point = MTPoint(14.4, 50.0)
        val k = 2.5
        val command = PointMult(point, k)
        assertEquals(
            "(() => { const p = new maptilersdk.Point(14.4, 50.0).mult(2.5); return { x: p.x, y: p.y }; })();",
            command.toJS(),
        )
    }
}
