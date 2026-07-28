package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointMatMultTest {
    @Test
    fun `toJS returns proper string`() {
        val point = MTPoint(14.4, 50.0)
        val matrix = listOf(1.0, 2.0, 3.0, 4.0)
        val command = PointMatMult(point, matrix)
        assertEquals(
            "(() => { const p = new maptilersdk.Point(14.4, 50.0).matMult([1.0, 2.0, 3.0, 4.0]); return { x: p.x, y: p.y }; })();",
            command.toJS(),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `init throws on invalid matrix size`() {
        val point = MTPoint(14.4, 50.0)
        val matrix = listOf(1.0, 2.0, 3.0)
        PointMatMult(point, matrix)
    }
}
