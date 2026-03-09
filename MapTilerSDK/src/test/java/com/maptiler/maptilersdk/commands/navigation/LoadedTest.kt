package com.maptiler.maptilersdk.commands.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoadedTest {
    @Test
    fun `toJS returns proper string`() {
        val command = Loaded()
        assertTrue(command.isPrimitiveReturnType)
        assertEquals(
            "map.loaded();",
            command.toJS(),
        )
    }
}
