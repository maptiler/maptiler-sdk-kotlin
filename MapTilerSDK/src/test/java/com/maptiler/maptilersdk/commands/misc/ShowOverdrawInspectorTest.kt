package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ShowOverdrawInspectorTest {
    @Test
    fun `showOverdrawInspector emits expected invocation`() {
        val commandTrue = ShowOverdrawInspector(true)
        val commandFalse = ShowOverdrawInspector(false)

        assertEquals("map.showOverdrawInpector = true;", commandTrue.toJS())
        assertEquals("map.showOverdrawInpector = false;", commandFalse.toJS())
        assertFalse(commandTrue.isPrimitiveReturnType)
    }
}
