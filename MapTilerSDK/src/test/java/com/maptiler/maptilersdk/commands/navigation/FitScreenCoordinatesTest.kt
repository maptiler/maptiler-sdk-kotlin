package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTFitBoundsOptions
import com.maptiler.maptilersdk.map.options.MTPaddingOptions
import com.maptiler.maptilersdk.map.types.MTPoint
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class FitScreenCoordinatesTest {
    private val p0 = MTPoint(100.0, 100.0)
    private val p1 = MTPoint(200.0, 200.0)
    private val bearing = 45.0

    @Test
    fun `fitScreenCoordinates without options serializes points and bearing`() {
        val command = FitScreenCoordinates(p0, p1, bearing, null)

        val expected = "map.fitScreenCoordinates({\"x\":100.0,\"y\":100.0},{\"x\":200.0,\"y\":200.0},45.0);"
        assertEquals(expected, command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }

    @Test
    fun `fitScreenCoordinates with options serializes configuration`() {
        val options =
            MTFitBoundsOptions(
                padding = MTPaddingOptions(1.0, 2.0, 3.0, 4.0),
                maxZoom = 12.0,
                linear = true,
                duration = 250.0,
            )
        val command = FitScreenCoordinates(p0, p1, bearing, options)
        val optionsJson = JsonConfig.json.encodeToString(options)

        val expected = "map.fitScreenCoordinates({\"x\":100.0,\"y\":100.0},{\"x\":200.0,\"y\":200.0},45.0,$optionsJson);"
        assertEquals(expected, command.toJS())
    }
}
