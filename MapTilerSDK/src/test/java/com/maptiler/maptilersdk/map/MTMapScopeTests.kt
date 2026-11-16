/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.bridge.MTCommandExecutable
import com.maptiler.maptilersdk.bridge.MTError
import com.maptiler.maptilersdk.commands.style.AddLayer
import com.maptiler.maptilersdk.commands.style.AddSource
import com.maptiler.maptilersdk.commands.style.IsSourceLoaded
import com.maptiler.maptilersdk.commands.style.RemoveLayer
import com.maptiler.maptilersdk.commands.style.RemoveSource
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTStyle
import com.maptiler.maptilersdk.map.style.layer.fill.MTFillLayer
import com.maptiler.maptilersdk.map.style.source.MTVectorTileSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URL

class MTMapScopeTests {
    private class RecordingExecutor : MTCommandExecutable {
        val commands = mutableListOf<MTCommand>()

        override suspend fun execute(command: MTCommand): MTBridgeReturnType {
            commands.add(command)
            return when (command) {
                is IsSourceLoaded -> MTBridgeReturnType.BoolValue(true)
                else -> MTBridgeReturnType.Null
            }
        }
    }

    private fun makeStyle(
        executor: RecordingExecutor,
        scope: CoroutineScope,
    ): MTStyle =
        MTStyle(MTMapReferenceStyle.STREETS).apply {
            initWorker(MTBridge(executor), scope)
        }

    @Test
    fun attachSourceAndLayer_addsThenDetachRemoves() =
        runBlocking {
            val executor = RecordingExecutor()
            val style = makeStyle(executor, this)
            val source = MTVectorTileSource("src-1", URL("https://example.com/tiles.json"))
            val layer = MTFillLayer("layer-1", source.identifier)

            assertTrue(style.attachSource(source))
            assertTrue(style.attachLayer(layer))
            yield()

            style.detachLayer(layer)
            style.detachSource(source)
            yield()

            val relevant =
                executor.commands.filter {
                    it is AddSource || it is AddLayer || it is RemoveLayer || it is RemoveSource
                }
            assertEquals(4, relevant.size)
            assertTrue(relevant[0] is AddSource)
            assertTrue(relevant[1] is AddLayer)
            assertTrue(relevant[2] is RemoveLayer)
            assertTrue(relevant[3] is RemoveSource)
        }

    @Test
    fun attachDuplicates_returnFalseWithoutThrowing() =
        runBlocking {
            val style = makeStyle(RecordingExecutor(), this)
            val source = MTVectorTileSource("src-1", URL("https://example.com/tiles.json"))
            val layer = MTFillLayer("layer-1", source.identifier)

            assertTrue(style.attachSource(source))
            assertFalse(style.attachSource(source))
            assertTrue(style.attachLayer(layer))
            assertFalse(style.attachLayer(layer))
        }

    @Test
    fun attachLayer_withoutSource_returnsFalse() =
        runBlocking {
            val style = makeStyle(RecordingExecutor(), this)

            assertFalse(style.attachLayer(MTFillLayer("layer-1", "missing-source")))
        }

    @Test
    fun addLayer_withoutSource_throwsSynchronously() =
        runBlocking<Unit> {
            val style = makeStyle(RecordingExecutor(), this)

            assertThrows(MTError.MissingParent::class.java) {
                style.addLayer(MTFillLayer("layer-1", "missing-source"))
            }
        }

    @Test
    fun detach_afterStyleChange_doesNotThrow() =
        runBlocking {
            val style = makeStyle(RecordingExecutor(), this)
            val source = MTVectorTileSource("src-1", URL("https://example.com/tiles.json"))
            val layer = MTFillLayer("layer-1", source.identifier)

            style.attachSource(source)
            style.attachLayer(layer)
            yield()

            style.setStyle(MTMapReferenceStyle.BACKDROP)

            style.detachLayer(layer)
            style.detachSource(source)
        }
}
