package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MTConfigTest {
    @Before
    fun setup() {
        MTConfig.setApplicationIdentifier("")
        MTConfig.useEuEndpoints = false
    }

    @Test
    fun testDefaultUserAgent() {
        assertEquals("MapTiler-Mobile-SDK-Android/${MTConfig.VERSION}", MTConfig.customUserAgent)
    }

    @Test
    fun testCustomUserAgentSanitization() {
        // Test strict allowlist: alphanumeric, period, hyphen, underscore
        val maliciousId = "com.example.app\r\nInject: Header\tX🔥 /\\;*"
        MTConfig.setApplicationIdentifier(maliciousId)

        assertEquals(
            "com.example.appInjectHeaderX MapTiler-Mobile-SDK-Android/${MTConfig.VERSION}",
            MTConfig.customUserAgent,
        )
    }

    @Test
    fun testDefaultApiEndpoints() {
        assertFalse(MTConfig.useEuEndpoints)
        assertEquals("api.maptiler.com", MTConfig.apiHost)
        assertEquals("https://api.maptiler.com/", MTConfig.apiURL)
    }

    @Test
    fun testEuApiEndpoints() {
        MTConfig.useEuEndpoints = true

        assertTrue(MTConfig.useEuEndpoints)
        assertEquals("api.maptiler.eu", MTConfig.apiHost)
        assertEquals("https://api.maptiler.eu/", MTConfig.apiURL)

        MTConfig.useEuEndpoints = false

        assertEquals("api.maptiler.com", MTConfig.apiHost)
        assertEquals("https://api.maptiler.com/", MTConfig.apiURL)
    }

    @Test
    fun testReferenceStyleUrlUsesConfiguredHost() {
        MTConfig.useEuEndpoints = true

        assertEquals(
            "https://api.maptiler.eu/maps/streets-v4/style.json?key=KEY",
            MTMapReferenceStyle.STREETS.fetchStyleUrl(apiKey = "KEY").toString(),
        )
    }
}
