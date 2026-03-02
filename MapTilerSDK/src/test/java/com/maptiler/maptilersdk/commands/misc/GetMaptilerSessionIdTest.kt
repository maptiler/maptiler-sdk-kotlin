package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetMaptilerSessionIdTest {
    @Test
    fun `getMaptilerSessionId emits expected invocation`() {
        val command = GetMaptilerSessionId()

        assertEquals("map.getMaptilerSessionId();", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }
}
