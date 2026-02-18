package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTAnimationOptions
import com.maptiler.maptilersdk.map.types.MTPoint
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Test

class ZoomToTest {
    @Test
    fun `zoomTo without options serializes zoom`() {
        val command = ZoomTo(10.0)

        assertEquals("map.zoomTo(10.0, {});", command.toJS())
    }

    @Test
    fun `zoomTo with options serializes configuration`() {
        val options =
            MTAnimationOptions(
                duration = 2000.0,
                offset = MTPoint(10.0, 20.0),
                animate = true,
                essential = false,
            )
        val command = ZoomTo(10.0, options)
        val optionsJson = JsonConfig.json.encodeToString(options)

        assertEquals("map.zoomTo(10.0, $optionsJson);", command.toJS())
    }
}
