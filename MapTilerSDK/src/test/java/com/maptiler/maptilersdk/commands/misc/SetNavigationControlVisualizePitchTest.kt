package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Test

class SetNavigationControlVisualizePitchTest {
    @Test
    fun testToJS() {
        val command = SetNavigationControlVisualizePitch(true)
        assertEquals("map.navigationControl.visualizePitch = true;", command.toJS())
    }
}
