package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ShowTileBoundariesTest {
    @Test
    fun `showTileBoundaries emits expected invocation`() {
        val commandTrue = ShowTileBoundaries(true)
        val commandFalse = ShowTileBoundaries(false)

        assertEquals("map.showTileBoundaries = true;", commandTrue.toJS())
        assertEquals("map.showTileBoundaries = false;", commandFalse.toJS())
        assertFalse(commandTrue.isPrimitiveReturnType)
    }
}
