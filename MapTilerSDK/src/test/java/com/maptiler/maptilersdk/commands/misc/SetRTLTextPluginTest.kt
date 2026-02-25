package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SetRTLTextPluginTest {
    @Test
    fun `setRTLTextPlugin emits expected invocation`() {
        val command = SetRTLTextPlugin("https://example.com/plugin.js", false)
        val commandWithDeferred = SetRTLTextPlugin("https://example.com/plugin2.js", true)

        assertEquals("map.setRTLTextPlugin(\"https://example.com/plugin.js\", null, false);", command.toJS())
        assertEquals("map.setRTLTextPlugin(\"https://example.com/plugin2.js\", null, true);", commandWithDeferred.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }
}
