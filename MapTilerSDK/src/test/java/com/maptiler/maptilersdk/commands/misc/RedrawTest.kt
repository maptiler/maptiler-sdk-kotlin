package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RedrawTest {
    @Test
    fun `redraw emits expected invocation`() {
        val command = Redraw()

        assertEquals("map.redraw();", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }
}
