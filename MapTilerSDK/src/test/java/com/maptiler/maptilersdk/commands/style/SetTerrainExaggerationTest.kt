package com.maptiler.maptilersdk.commands.style

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SetTerrainExaggerationTest {
    @Test
    fun `toJS returns proper string with exaggeration only`() {
        val command = SetTerrainExaggeration(2.0)
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.setTerrainExaggeration(2.0);",
            command.toJS(),
        )
    }

    @Test
    fun `toJS returns proper string with exaggeration and animate`() {
        val command = SetTerrainExaggeration(2.0, false)
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.setTerrainExaggeration(2.0, false);",
            command.toJS(),
        )
    }
}
