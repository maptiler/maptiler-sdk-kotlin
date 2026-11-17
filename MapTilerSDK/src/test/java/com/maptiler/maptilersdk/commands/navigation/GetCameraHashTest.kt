package com.maptiler.maptilersdk.commands.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetCameraHashTest {
    @Test
    fun `getCameraHash emits expected JS`() {
        val command = GetCameraHash()
        assertEquals("map.getCameraHash();", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }
}
