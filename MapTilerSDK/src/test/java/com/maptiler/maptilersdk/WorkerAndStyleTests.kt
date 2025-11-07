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
import com.maptiler.maptilersdk.commands.navigation.GetCenterClampedToGround
import com.maptiler.maptilersdk.commands.navigation.GetCenterElevation
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTStyle
import com.maptiler.maptilersdk.map.style.MTStyleError
import com.maptiler.maptilersdk.map.style.layer.fill.MTFillLayer
import com.maptiler.maptilersdk.map.workers.navigable.NavigableWorker
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kotlin.collections.ArrayDeque
import org.junit.Assert.assertEquals
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

    @Test fun navigableWorker_getCenterClampedToGround_ParsesPrimitiveReturnTypes() =
        runBlocking {
            val executedCommands = mutableListOf<MTCommand>()
            val responses =
                ArrayDeque(
                    listOf(
                        MTBridgeReturnType.StringValue("true"),
                        MTBridgeReturnType.DoubleValue(0.0),
                        MTBridgeReturnType.BoolValue(false),
                    ),
                )

            val exec =
                object : MTCommandExecutable {
                    override suspend fun execute(command: MTCommand): MTBridgeReturnType {
                        executedCommands.add(command)
                        return responses.removeFirst()
                    }
                }

            val bridge = MTBridge(exec)
            val worker = NavigableWorker(bridge, this)
            val first = worker.getCenterClampedToGround()
            val second = worker.getCenterClampedToGround()
            val third = worker.getCenterClampedToGround()

            assertEquals(true, first)
            assertEquals(false, second)
            assertEquals(false, third)
            assertTrue(executedCommands.all { it is GetCenterClampedToGround })
        }

    @Test fun navigableWorker_getCenterElevation_ParsesStringReturnType() =
        runBlocking {
            var usedGetCenterElevation = false
            val exec =
                object : MTCommandExecutable {
                    override suspend fun execute(command: MTCommand): MTBridgeReturnType {
                        if (command is GetCenterElevation) {
                            usedGetCenterElevation = true
                        }
                        return MTBridgeReturnType.StringValue("42.5")
                    }
                }
            val bridge = MTBridge(exec)
            val worker = NavigableWorker(bridge, this)

            val result = worker.getCenterElevation()
            assertEquals(42.5, result, 0.0)
            assertTrue(usedGetCenterElevation)
        }

    @Test fun mtStyle_removeLayer_ThrowsWhenMissing() {
        val style = MTStyle(MTMapReferenceStyle.STREETS)
        val layer = MTFillLayer("layer-1", "src-1")

        assertThrows(MTStyleError.LayerNotFound::class.java) {
            style.removeLayer(layer)
        }
    }
}
