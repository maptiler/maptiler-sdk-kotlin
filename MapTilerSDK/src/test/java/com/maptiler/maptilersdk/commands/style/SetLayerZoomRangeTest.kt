package com.maptiler.maptilersdk.commands.style

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SetLayerZoomRangeTest {
    @Test
    fun `toJS returns proper string`() {
        val command = SetLayerZoomRange("water-layer", 2.0, 10.0)
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.setLayerZoomRange(\"water-layer\", 2.0, 10.0);",
            command.toJS(),
        )
    }

    @Test
    fun `toJS returns proper string with minzoom`() {
        val command = SetLayerZoomRange("water-layer", 0.0, 22.0)
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.setLayerZoomRange(\"water-layer\", 0.0, 22.0);",
            command.toJS(),
        )
    }
}
