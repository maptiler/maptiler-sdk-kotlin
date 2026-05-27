package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Test

class PrewarmTest {
    @Test
    fun `toJS returns proper string`() {
        val command = Prewarm()
        assertEquals("maptilersdk.prewarm();", command.toJS())
    }
}
