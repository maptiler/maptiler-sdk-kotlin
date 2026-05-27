package com.maptiler.maptilersdk.commands

import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import org.junit.Assert.assertEquals
import org.junit.Test

class InitializeMapTest {
    @Test
    fun `toJS matches signature with prewarm`() {
        val options = MTMapOptions(prewarm = true)
        val command = InitializeMap("apikey", options, MTMapReferenceStyle.STREETS, null, true)
        val expectedOptionsStr =
            "{\"logoPosition\":\"top-left\",\"eventLevel\":\"CAMERA_ONLY\"," +
                "\"highFrequencyEventThrottleMs\":20,\"minimap\":false,\"geolocateControl\":false," +
                "\"navigationControl\":false,\"projectionControl\":false,\"scaleControl\":false," +
                "\"terrainControl\":false,\"prewarm\":true,\"isSessionLogicEnabled\":true}"
        val expected =
            "maptilersdk.prewarm(); initializeMap('apikey', maptilersdk.MapStyle.STREETS, " +
                "$expectedOptionsStr, true, 'CAMERA_ONLY', 20); if (typeof map !== 'undefined' " +
                "&& map.telemetry) { map.telemetry.registerModule('maptiler-sdk-android', " +
                "'${MTConfig.VERSION}'); }"
        assertEquals(expected, command.toJS())
    }

    @Test
    fun `toJS matches signature without prewarm`() {
        val options = MTMapOptions(prewarm = false)
        val command = InitializeMap("apikey", options, MTMapReferenceStyle.STREETS, null, true)
        val expectedOptionsStr =
            "{\"logoPosition\":\"top-left\",\"eventLevel\":\"CAMERA_ONLY\"," +
                "\"highFrequencyEventThrottleMs\":20,\"minimap\":false,\"geolocateControl\":false," +
                "\"navigationControl\":false,\"projectionControl\":false,\"scaleControl\":false," +
                "\"terrainControl\":false,\"prewarm\":false,\"isSessionLogicEnabled\":true}"
        val expected =
            "initializeMap('apikey', maptilersdk.MapStyle.STREETS, " +
                "$expectedOptionsStr, true, 'CAMERA_ONLY', 20); if (typeof map !== 'undefined' " +
                "&& map.telemetry) { map.telemetry.registerModule('maptiler-sdk-android', " +
                "'${MTConfig.VERSION}'); }"
        assertEquals(expected, command.toJS())
    }
}
