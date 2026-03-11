package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTAnimationOptions
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class StopAndSnapToNorthCommandsTest {
    @Test
    fun `stop command emits expected invocation`() {
        val command = Stop()

        assertEquals("map.stop();", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }

    @Test
    fun `snapToNorth without options emits expected invocation`() {
        val command = SnapToNorth()

        assertEquals("map.snapToNorth();", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }

    @Test
    fun `snapToNorth with options serializes configuration`() {
        val options = MTAnimationOptions(duration = 1000.0, animate = true)
        val command = SnapToNorth(options)
        val optionsJson = JsonConfig.json.encodeToString(options)

        assertEquals("map.snapToNorth($optionsJson);", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }

    @Test
    fun `resetNorth without options emits expected invocation`() {
        val command = ResetNorth()

        assertEquals("map.resetNorth();", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }

    @Test
    fun `resetNorth with options serializes configuration`() {
        val options = MTAnimationOptions(duration = 1000.0, animate = true)
        val command = ResetNorth(options)
        val optionsJson = JsonConfig.json.encodeToString(options)

        assertEquals("map.resetNorth($optionsJson);", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }

    @Test
    fun `resetNorthPitch without options emits expected invocation`() {
        val command = ResetNorthPitch()

        assertEquals("map.resetNorthPitch();", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }

    @Test
    fun `resetNorthPitch with options serializes configuration`() {
        val options = MTAnimationOptions(duration = 1000.0, animate = true)
        val command = ResetNorthPitch(options)
        val optionsJson = JsonConfig.json.encodeToString(options)

        assertEquals("map.resetNorthPitch($optionsJson);", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }
}
