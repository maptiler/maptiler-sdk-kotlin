package com.maptiler.maptilersdk.commands.style

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IsStyleLoadedTest {
    @Test
    fun `toJS returns proper string`() {
        val command = IsStyleLoaded()
        assertTrue(command.isPrimitiveReturnType)
        assertEquals(
            "map.isStyleLoaded();",
            command.toJS(),
        )
    }
}
