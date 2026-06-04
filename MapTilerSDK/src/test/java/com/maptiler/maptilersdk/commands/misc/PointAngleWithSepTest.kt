package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class PointAngleWithSepTest {
    @Test
    fun `toJS returns proper string`() {
        val point = MTPoint(14.4, 50.0)
        val command = PointAngleWithSep(point, 1.0, 2.0)
        assertEquals(
            "new maptilersdk.Point(14.4, 50.0).angleWithSep(1.0, 2.0);",
            command.toJS(),
        )
    }
}
