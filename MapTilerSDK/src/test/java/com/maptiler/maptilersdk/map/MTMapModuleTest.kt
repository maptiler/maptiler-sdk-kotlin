package com.maptiler.maptilersdk.map

import android.content.Context
import com.maptiler.maptilersdk.map.style.MTStyle
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MTMapModuleTest {
    private class MockModule : MTMapModule {
        override val id: String = "test-module"
        var attachCalled = false
        var mapReadyCalled = false
        var lastReceivedEvent: String? = null
        var lastReceivedData: String? = null

        override fun onAttach(controller: MTMapViewController) {
            attachCalled = true
        }

        override fun onMapReady() {
            mapReadyCalled = true
        }

        override fun onMessageReceived(
            event: String,
            data: String?,
        ) {
            lastReceivedEvent = event
            lastReceivedData = data
        }
    }

    @Test
    fun testModuleRegistrationAndLifecycle() {
        val mockContext: Context = mockk(relaxed = true)
        val controller = MTMapViewController(mockContext)

        val mockModule = MockModule()

        // Test Registration
        controller.registerModule(mockModule)
        assertTrue("Module should be attached upon registration", mockModule.attachCalled)

        // Mock that the map is initialized by assigning a mock style
        val mockStyle: MTStyle = mockk(relaxed = true)
        controller.style = mockStyle

        // Register another module when map is already initialized
        val mockModule2 = MockModule()
        controller.registerModule(mockModule2)
        assertTrue("Module should be attached upon registration", mockModule2.attachCalled)
        assertTrue("Module should receive onMapReady immediately if map is ready", mockModule2.mapReadyCalled)
    }

    @Test
    fun testModuleEventRouting() {
        val mockContext: Context = mockk(relaxed = true)
        val controller = MTMapViewController(mockContext)
        val mockModule = MockModule()
        controller.registerModule(mockModule)

        // Route an event targeting the module
        controller.onModuleEvent("test-module", "customEvent", "{\"key\":\"value\"}")

        assertEquals("customEvent", mockModule.lastReceivedEvent)
        assertEquals("{\"key\":\"value\"}", mockModule.lastReceivedData)

        // Route an event with empty data, should pass null
        controller.onModuleEvent("test-module", "emptyEvent", "")
        assertEquals("emptyEvent", mockModule.lastReceivedEvent)
        assertNull("Empty data string should be passed as null", mockModule.lastReceivedData)
    }
}
