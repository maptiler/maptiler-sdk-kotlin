package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SetMaxParallelImageRequestsTest {
    @Test
    fun `setMaxParallelImageRequests emits expected invocation`() {
        val command = SetMaxParallelImageRequests(10)

        assertEquals("map._setMaxParallelImageRequests(10);", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }
}
