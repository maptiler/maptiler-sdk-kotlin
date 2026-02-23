package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.map.style.MTTerrainSpecification
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SetTerrainTest {
    @Test
    fun `toJS returns proper string with terrain`() {
        val command = SetTerrain(MTTerrainSpecification("maptiler_terrain", 1.5))
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.setTerrain({\"source\":\"maptiler_terrain\",\"exaggeration\":1.5});",
            command.toJS(),
        )
    }

    @Test
    fun `toJS returns proper string with null terrain`() {
        val command = SetTerrain(null)
        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.setTerrain(null);",
            command.toJS(),
        )
    }
}
