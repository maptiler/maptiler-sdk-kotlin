package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RepaintTest {
    @Test
    fun `repaint emits expected invocation`() {
        val command = Repaint()

        assertEquals("map.triggerRepaint();", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }
}
