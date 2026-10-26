package com.maptiler.maptilersdk.commands.misc

import org.junit.Assert.assertEquals
import org.junit.Test

class SetUseEuEndpointsTest {
    @Test
    fun `toJS sets useEuEndpoints on sdk config`() {
        assertEquals("maptilersdk.config.useEuEndpoints = true;", SetUseEuEndpoints(true).toJS())
        assertEquals("maptilersdk.config.useEuEndpoints = false;", SetUseEuEndpoints(false).toJS())
    }
}
