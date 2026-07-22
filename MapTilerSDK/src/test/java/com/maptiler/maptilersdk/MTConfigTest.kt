package com.maptiler.maptilersdk

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MTConfigTest {
    @Before
    fun setup() {
        MTConfig.setApplicationIdentifier("")
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
}
