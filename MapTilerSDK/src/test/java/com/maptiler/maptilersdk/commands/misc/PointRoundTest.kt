package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointRoundTest {
    @Test
    fun `toJS returns proper string`() {
        val point = MTPoint(1.5, 2.5)
        val command = PointRound(point)
        assertEquals(
            "(() => { const p = new maptilersdk.Point(1.5, 2.5).round(); return { x: p.x, y: p.y }; })();",
            command.toJS(),
        )
    }
}
