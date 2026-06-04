package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointAngleTest {
    @Test
    fun `toJS returns proper string`() {
        val point = MTPoint(14.4, 50.0)
        val command = PointAngle(point)
        assertEquals(
            "new maptilersdk.Point(14.4, 50.0).angle();",
            command.toJS(),
        )
    }
}
