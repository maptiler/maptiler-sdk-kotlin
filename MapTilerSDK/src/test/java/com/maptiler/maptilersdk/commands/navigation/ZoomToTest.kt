package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTAnimationOptions
import com.maptiler.maptilersdk.map.types.MTPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ZoomToTest {
    @Test
    fun `zoomTo without options serializes correctly`() {
        val command = ZoomTo(10.0, null)
        assertEquals("map.zoomTo(10.0, {});", command.toJS())
        assertFalse(command.isPrimitiveReturnType)
    }

    @Test
    fun `zoomTo with options serializes correctly`() {
        val options =
            MTAnimationOptions(
                duration = 1000,
                animate = true,
                essential = true,
                offset = MTPoint(10.0, 20.0),
            )
        val command = ZoomTo(10.0, options)
        val optionsJson = JsonConfig.json.encodeToString(options)

        // JsonConfig.json.encodeToString might produce {"duration":1000,"animate":true,"essential":true,"offset":[10.0,20.0]}
        // We compare against the actual JSON string to be safe against property ordering or formatting changes in serialization
        assertEquals("map.zoomTo(10.0, $optionsJson);", command.toJS())
    }
}
