package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointDivTest {
    @Test
    fun `toJS returns proper string`() {
        val point = MTPoint(14.4, 50.0)
        val scalar = 2.0
        val command = PointDiv(point, scalar)
        assertEquals(
            "(() => { const p = new maptilersdk.Point(14.4, 50.0).div(2.0); return { x: p.x, y: p.y }; })();",
            command.toJS(),
        )
    }
}
