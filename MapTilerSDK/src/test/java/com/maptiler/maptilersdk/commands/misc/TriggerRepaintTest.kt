package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TriggerRepaintTest {
    @Test
    fun `triggerRepaint emits expected invocation`() {
        val command = TriggerRepaint()

        assertEquals("map.triggerRepaint();", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }
}
