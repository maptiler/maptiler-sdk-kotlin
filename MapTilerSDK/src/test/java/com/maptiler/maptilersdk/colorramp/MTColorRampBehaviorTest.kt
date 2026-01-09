package com.maptiler.maptilersdk.colorramp

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.bridge.MTCommandExecutable
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private class FixedReturnExecutor(
    private val fixed: MTBridgeReturnType,
) : MTCommandExecutable {
    override suspend fun execute(command: MTCommand): MTBridgeReturnType = fixed
}

class MTColorRampBehaviorTest {
    @Test
    fun hasTransparentStart_acceptsBoolAndString() {
        runBlocking {
            val bridgeBool = MTBridge(FixedReturnExecutor(MTBridgeReturnType.BoolValue(true)))
            val rampBool = MTColorRamp("cr_bool", bridgeBool)
            assertTrue(rampBool.hasTransparentStart())

            val bridgeString = MTBridge(FixedReturnExecutor(MTBridgeReturnType.StringValue("\"true\"")))
            val rampString = MTColorRamp("cr_str", bridgeString)
            assertTrue(rampString.hasTransparentStart())

            val bridgeFalse = MTBridge(FixedReturnExecutor(MTBridgeReturnType.StringValue("\"false\"")))
            val rampFalse = MTColorRamp("cr_str_f", bridgeFalse)
            assertFalse(rampFalse.hasTransparentStart())
        }
    }

    @Test
    fun getBounds_parsesJsonString() {
        runBlocking {
            val json = "{\"min\":2.0,\"max\":9.0}"
            val bridge = MTBridge(FixedReturnExecutor(MTBridgeReturnType.StringValue(json)))
            val ramp = MTColorRamp("cr_bounds", bridge)
            val bounds = ramp.getBounds()
            assertEquals(2.0, bounds.min, 0.0)
            assertEquals(9.0, bounds.max, 0.0)
        }
    }
}
