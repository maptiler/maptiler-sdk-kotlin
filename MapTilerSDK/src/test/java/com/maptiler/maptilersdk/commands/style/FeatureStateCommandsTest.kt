package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.map.types.MTFeatureIdentifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureStateCommandsTest {
    @Test
    fun `SetFeatureState toJS contract`() {
        val feature = MTFeatureIdentifier("my-source", "my-layer", "1")
        val stateJson = """{"hover":true}"""
        val command = SetFeatureState(feature, stateJson)

        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.setFeatureState({\"source\":\"my-source\",\"sourceLayer\":\"my-layer\",\"id\":\"1\"}, {\"hover\":true});",
            command.toJS(),
        )
    }

    @Test
    fun `GetFeatureState toJS contract`() {
        val feature = MTFeatureIdentifier("my-source", null, "1")
        val command = GetFeatureState(feature)

        assertTrue(command.isPrimitiveReturnType)
        assertEquals(
            "JSON.stringify(map.getFeatureState({\"source\":\"my-source\",\"id\":\"1\"}));",
            command.toJS(),
        )
    }

    @Test
    fun `RemoveFeatureState toJS contract without key`() {
        val feature = MTFeatureIdentifier("my-source", "my-layer", "1")
        val command = RemoveFeatureState(feature)

        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.removeFeatureState({\"source\":\"my-source\",\"sourceLayer\":\"my-layer\",\"id\":\"1\"});",
            command.toJS(),
        )
    }

    @Test
    fun `RemoveFeatureState toJS contract with key`() {
        val feature = MTFeatureIdentifier("my-source", "my-layer", "1")
        val command = RemoveFeatureState(feature, "hover")

        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "map.removeFeatureState({\"source\":\"my-source\",\"sourceLayer\":\"my-layer\",\"id\":\"1\"}, \"hover\");",
            command.toJS(),
        )
    }
}
