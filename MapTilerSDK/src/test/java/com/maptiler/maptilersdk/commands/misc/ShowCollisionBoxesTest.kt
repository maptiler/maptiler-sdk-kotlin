package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ShowCollisionBoxesTest {
    @Test
    fun `showCollisionBoxes emits expected invocation`() {
        val commandTrue = ShowCollisionBoxes(true)
        val commandFalse = ShowCollisionBoxes(false)

        assertEquals("map.showCollisionBoxes = true;", commandTrue.toJS())
        assertEquals("map.showCollisionBoxes = false;", commandFalse.toJS())
        assertFalse(commandTrue.isPrimitiveReturnType)
    }
}
