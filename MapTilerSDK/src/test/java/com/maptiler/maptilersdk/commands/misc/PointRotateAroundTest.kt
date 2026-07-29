package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointRotateAroundTest {
    @Test
    fun `toJS returns proper string`() {
        val point = MTPoint(14.4, 50.0)
        val angle = 1.5708
        val pivot = MTPoint(10.0, 40.0)
        val command = PointRotateAround(point, angle, pivot)
        assertEquals(
            "(() => { const p = new maptilersdk.Point(14.4, 50.0)" +
                ".rotateAround(1.5708, new maptilersdk.Point(10.0, 40.0)); " +
                "return { x: p.x, y: p.y }; })();",
            command.toJS(),
        )
    }
}
