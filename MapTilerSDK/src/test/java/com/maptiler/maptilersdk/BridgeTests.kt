/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.bridge.MTCommandExecutable
import com.maptiler.maptilersdk.commands.navigation.GetZoom
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test

class BridgeTests {
    @Test fun execute_ReturnsExpectedType() =
        runBlocking {
            val expectedReturnType = MTBridgeReturnType.UnsupportedType
            val mockExecutor =
                object : MTCommandExecutable {
                    override suspend fun execute(command: MTCommand): MTBridgeReturnType = expectedReturnType
                }

            val bridge = MTBridge(mockExecutor)
            val command = GetZoom()

            val result = bridge.execute(command)

            assertEquals(expectedReturnType, result)
        }
}
