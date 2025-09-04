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
import com.maptiler.maptilersdk.commands.navigation.GetBearing
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTStyle
import com.maptiler.maptilersdk.map.style.MTStyleError
import com.maptiler.maptilersdk.map.style.layer.fill.MTFillLayer
import com.maptiler.maptilersdk.map.workers.navigable.NavigableWorker
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Test

class WorkerAndStyleTests {
    @Test fun navigableWorker_getBearing_UsesGetBearingCommand() =
        runBlocking {
            var usedGetBearing = false
            val exec =
                object : MTCommandExecutable {
                    override suspend fun execute(command: MTCommand): MTBridgeReturnType {
                        if (command is GetBearing) usedGetBearing = true
                        return MTBridgeReturnType.DoubleValue(0.0)
                    }
                }

            val bridge = MTBridge(exec)
            val worker = NavigableWorker(bridge, this)
            worker.getBearing()
            assertTrue(usedGetBearing)
        }

    @Test fun mtStyle_removeLayer_ThrowsWhenMissing() {
        val style = MTStyle(MTMapReferenceStyle.STREETS)
        val layer = MTFillLayer("layer-1", "src-1")

        assertThrows(MTStyleError.LayerNotFound::class.java) {
            style.removeLayer(layer)
        }
    }
}
