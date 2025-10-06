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
import com.maptiler.maptilersdk.commands.style.IsSourceLoaded
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTStyle
import com.maptiler.maptilersdk.map.style.MTStyleError
import com.maptiler.maptilersdk.map.style.layer.fill.MTFillLayer
import com.maptiler.maptilersdk.map.style.source.MTVectorTileSource
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertThrows
import org.junit.Test
import java.net.URL

class MTStyleSetStyleTests {
    @Test
    fun setStyle_clearsSourcesAndLayersCaches() =
        runBlocking {
            // Bridge that returns `true` for IsSourceLoaded, Null otherwise
            val exec =
                object : MTCommandExecutable {
                    override suspend fun execute(command: MTCommand): MTBridgeReturnType =
                        when (command) {
                            is IsSourceLoaded -> MTBridgeReturnType.BoolValue(true)
                            else -> MTBridgeReturnType.Null
                        }
                }

            val style = MTStyle(MTMapReferenceStyle.STREETS)
            style.initWorker(MTBridge(exec), this)

            // Add a source and a layer referencing it
            val src = MTVectorTileSource("src-1", URL("https://example.com/style.json"))
            style.addSource(src)

            val layer = MTFillLayer("layer-1", src.identifier)
            style.addLayer(layer)

            // Yield to allow launched coroutines to run (caches update happens in addLayer coroutine)
            yield()

            // Apply a new style which clears caches
            style.setStyle(MTMapReferenceStyle.BACKDROP, null)

            // Removing previously added entities should now throw NotFound errors (caches are cleared)
            assertThrows(MTStyleError.SourceNotFound::class.java) { style.removeSource(src) }
            assertThrows(MTStyleError.LayerNotFound::class.java) { style.removeLayer(layer) }

            // Sanity: style is still usable after change
            assertNotNull(style.getVariantsForCurrentReferenceStyle())
        }
}
