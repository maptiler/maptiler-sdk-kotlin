package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointRotateTest {
    @Test
    fun `toJS returns proper string`() {
        val point = MTPoint(14.4, 50.0)
        val angle = 1.5708
        val command = PointRotate(point, angle)
        assertEquals(
            "(() => { const p = new maptilersdk.Point(14.4, 50.0).rotate(1.5708); return { x: p.x, y: p.y }; })();",
            command.toJS(),
        )
    }
}
