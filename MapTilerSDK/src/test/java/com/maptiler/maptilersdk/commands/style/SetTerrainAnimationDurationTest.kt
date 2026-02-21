package com.maptiler.maptilersdk.commands.style

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SetTerrainAnimationDurationTest {
    @Test
    fun `toJS returns proper string`() {
        val command = SetTerrainAnimationDuration(1500.0)
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.setTerrainAnimationDuration(1500.0);",
            command.toJS(),
        )
    }
}
