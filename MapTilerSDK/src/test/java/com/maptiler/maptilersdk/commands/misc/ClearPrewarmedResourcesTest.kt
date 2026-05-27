package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Test

class ClearPrewarmedResourcesTest {
    @Test
    fun `toJS returns proper string`() {
        val command = ClearPrewarmedResources()
        assertEquals("maptilersdk.clearPrewarmedResources();", command.toJS())
    }
}
