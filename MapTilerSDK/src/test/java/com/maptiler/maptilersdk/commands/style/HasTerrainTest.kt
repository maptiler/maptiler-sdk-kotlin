package com.maptiler.maptilersdk.commands.style

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HasTerrainTest {
    @Test
    fun `toJS returns proper string`() {
        val command = HasTerrain
        assertTrue(command.isPrimitiveReturnType)
        assertEquals(
            "map.hasTerrain();",
            command.toJS(),
        )
    }
}
