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
import com.maptiler.maptilersdk.commands.navigation.AreTilesLoaded
import com.maptiler.maptilersdk.commands.navigation.CenterOnIpPoint
import com.maptiler.maptilersdk.commands.navigation.GetBearing
import com.maptiler.maptilersdk.commands.navigation.GetCenterClampedToGround
import com.maptiler.maptilersdk.commands.navigation.GetCenterElevation
import com.maptiler.maptilersdk.commands.navigation.GetRenderWorldCopies
import com.maptiler.maptilersdk.commands.style.GetProjection
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTStyle
import com.maptiler.maptilersdk.map.style.MTStyleError
import com.maptiler.maptilersdk.map.style.layer.fill.MTFillLayer
import com.maptiler.maptilersdk.map.types.MTProjectionType
import com.maptiler.maptilersdk.map.workers.navigable.NavigableWorker
import com.maptiler.maptilersdk.map.workers.stylable.StylableWorker
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.collections.ArrayDeque

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

    @Test fun navigableWorker_centerOnIpPoint_UsesCenterOnIpPointCommand() =
        runBlocking {
            var usedCenterOnIpPoint = false
            val exec =
                object : MTCommandExecutable {
                    override suspend fun execute(command: MTCommand): MTBridgeReturnType {
                        if (command is CenterOnIpPoint) usedCenterOnIpPoint = true
                        return MTBridgeReturnType.Null
                    }
                }

            val bridge = MTBridge(exec)
            val worker = NavigableWorker(bridge, this)
            worker.centerOnIpPoint()
            assertTrue(usedCenterOnIpPoint)
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

    @Test fun navigableWorker_getRenderWorldCopies_ParsesPrimitiveReturnTypes() =
        runBlocking {
            val executedCommands = mutableListOf<MTCommand>()
            val responses =
                ArrayDeque(
                    listOf(
                        MTBridgeReturnType.BoolValue(true),
                        MTBridgeReturnType.StringValue("false"),
                        MTBridgeReturnType.StringValue("1"),
                        MTBridgeReturnType.DoubleValue(0.0),
                        MTBridgeReturnType.DoubleValue(2.0),
                        MTBridgeReturnType.StringValue("  TRUE "),
                        MTBridgeReturnType.StringValue("invalid"),
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

            val first = worker.getRenderWorldCopies()
            val second = worker.getRenderWorldCopies()
            val third = worker.getRenderWorldCopies()
            val fourth = worker.getRenderWorldCopies()
            val fifth = worker.getRenderWorldCopies()
            val sixth = worker.getRenderWorldCopies()
            val seventh = worker.getRenderWorldCopies()

            assertEquals(true, first)
            assertEquals(false, second)
            assertEquals(true, third)
            assertEquals(false, fourth)
            assertEquals(true, fifth)
            assertEquals(true, sixth)
            assertEquals(false, seventh)
            assertTrue(executedCommands.all { it is GetRenderWorldCopies })
        }

    @Test fun navigableWorker_areTilesLoaded_ParsesPrimitiveReturnTypes() =
        runBlocking {
            val executedCommands = mutableListOf<MTCommand>()
            val responses =
                ArrayDeque(
                    listOf(
                        MTBridgeReturnType.BoolValue(true),
                        MTBridgeReturnType.DoubleValue(0.0),
                        MTBridgeReturnType.DoubleValue(1.0),
                        MTBridgeReturnType.StringValue("false"),
                        MTBridgeReturnType.StringValue("  TRUE  "),
                        MTBridgeReturnType.StringValue("0"),
                        MTBridgeReturnType.StringValue("unexpected"),
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

            val first = worker.areTilesLoaded()
            val second = worker.areTilesLoaded()
            val third = worker.areTilesLoaded()
            val fourth = worker.areTilesLoaded()
            val fifth = worker.areTilesLoaded()
            val sixth = worker.areTilesLoaded()
            val seventh = worker.areTilesLoaded()

            assertEquals(true, first)
            assertEquals(false, second)
            assertEquals(true, third)
            assertEquals(false, fourth)
            assertEquals(true, fifth)
            assertEquals(false, sixth)
            assertEquals(false, seventh)
            assertTrue(executedCommands.all { it is AreTilesLoaded })
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

    @Test fun stylableWorker_getProjection_UsesGetProjectionCommand() =
        runBlocking {
            var usedGetProjection = false
            val exec =
                object : MTCommandExecutable {
                    override suspend fun execute(command: MTCommand): MTBridgeReturnType {
                        if (command is GetProjection) {
                            usedGetProjection = true
                        }
                        return MTBridgeReturnType.StringValue("\"globe\"")
                    }
                }
            val bridge = MTBridge(exec)
            val worker = StylableWorker(bridge, this)

            val result = worker.getProjection()
            assertTrue(usedGetProjection)
            assertEquals(MTProjectionType.GLOBE, result)
        }

    @Test fun stylableWorker_getProjection_DefaultsToMercatorWhenNull() =
        runBlocking {
            val exec =
                object : MTCommandExecutable {
                    override suspend fun execute(command: MTCommand): MTBridgeReturnType = MTBridgeReturnType.Null
                }
            val bridge = MTBridge(exec)
            val worker = StylableWorker(bridge, this)

            val result = worker.getProjection()
            assertEquals(MTProjectionType.MERCATOR, result)
        }

    @Test fun mtStyle_removeLayer_ThrowsWhenMissing() {
        val style = MTStyle(MTMapReferenceStyle.STREETS)
        val layer = MTFillLayer("layer-1", "src-1")

        assertThrows(MTStyleError.LayerNotFound::class.java) {
            style.removeLayer(layer)
        }
    }
}
