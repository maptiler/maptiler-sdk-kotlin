package com.maptiler.maptilersdk.commands.style

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IsGlobeProjectionTest {
    @Test
    fun `toJS returns proper string`() {
        val command = IsGlobeProjection()
        assertTrue(command.isPrimitiveReturnType)
        assertEquals(
            "map.isGlobeProjection();",
            command.toJS(),
        )
    }
}
