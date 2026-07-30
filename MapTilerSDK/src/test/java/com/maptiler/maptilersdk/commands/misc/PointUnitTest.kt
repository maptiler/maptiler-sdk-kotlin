package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointUnitTest {
    @Test
    fun `toJS returns proper string`() {
        val point = MTPoint(3.0, 4.0)
        val command = PointUnit(point)
        assertEquals(
            "(() => { const p = new maptilersdk.Point(3.0, 4.0).unit(); return { x: p.x, y: p.y }; })();",
            command.toJS(),
        )
    }
}
