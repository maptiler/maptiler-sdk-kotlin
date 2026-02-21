package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ShowPaddingTest {
    @Test
    fun `showPadding emits expected invocation`() {
        val commandTrue = ShowPadding(true)
        val commandFalse = ShowPadding(false)

        assertEquals("map.showPadding = true;", commandTrue.toJS())
        assertEquals("map.showPadding = false;", commandFalse.toJS())
        assertFalse(commandTrue.isPrimitiveReturnType)
    }
}
