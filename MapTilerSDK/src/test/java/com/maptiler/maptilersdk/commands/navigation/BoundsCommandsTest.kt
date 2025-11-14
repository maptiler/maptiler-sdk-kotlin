package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.options.MTFitBoundsOptions
import com.maptiler.maptilersdk.map.options.MTPaddingOptions
import com.maptiler.maptilersdk.map.types.MTBounds
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BoundsCommandsTest {
    private val sampleBounds =
        MTBounds(
            southwest = LngLat(-10.0, -5.0),
            northeast = LngLat(10.0, 5.0),
        )

    @Test
    fun `fitBounds without options serializes bounds`() {
        val command = FitBounds(sampleBounds, null)

        assertEquals("map.fitBounds([[-10.0,-5.0],[10.0,5.0]]);", command.toJS())
        assertTrue(!command.isPrimitiveReturnType)
    }

    @Test
    fun `fitBounds with options serializes configuration`() {
        val options =
            MTFitBoundsOptions(
                padding = MTPaddingOptions(1.0, 2.0, 3.0, 4.0),
                maxZoom = 12.0,
                linear = true,
                duration = 250.0,
            )
        val command = FitBounds(sampleBounds, options)
        val optionsJson = JsonConfig.json.encodeToString(options)

        assertEquals("map.fitBounds([[-10.0,-5.0],[10.0,5.0]],$optionsJson);", command.toJS())
    }

    @Test
    fun `fitToIpBounds emits expected invocation`() {
        val command = FitToIpBounds()

        assertEquals("map.fitToIpBounds();", command.toJS())
        assertTrue(!command.isPrimitiveReturnType)
    }

    @Test
    fun `centerOnIpPoint emits expected invocation`() {
        val command = CenterOnIpPoint()

        assertEquals("map.centerOnIpPoint();", command.toJS())
        assertTrue(!command.isPrimitiveReturnType)
    }

    @Test
    fun `getBounds uses primitive return`() {
        val command = GetBounds()

        assertEquals("map.getBounds();", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }

    @Test
    fun `setMaxBounds serializes bounds`() {
        val command = SetMaxBounds(sampleBounds)
        val boundsJson = JsonConfig.json.encodeToString(sampleBounds)

        assertEquals("map.setMaxBounds($boundsJson);", command.toJS())
    }

    @Test
    fun `setMaxBounds null clears bounds`() {
        val command = SetMaxBounds(null)

        assertEquals("map.setMaxBounds(null);", command.toJS())
    }

    @Test
    fun `getMaxBounds uses primitive return`() {
        val command = GetMaxBounds()

        assertEquals("map.getMaxBounds();", command.toJS())
        assertTrue(command.isPrimitiveReturnType)
    }
}
