/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.events.MTEvent
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.MTMapViewDelegate
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import com.maptiler.maptilersdk.map.style.layer.fill.MTFillLayer
import com.maptiler.maptilersdk.map.style.source.MTVectorTileSource
import com.maptiler.maptilersdk.map.types.MTData
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.net.URL
import java.util.Locale

/**
 * Basic benchmarking helper for Kotlin SDK.
 */
class MTBenchmark(
    context: Context,
) : MTMapViewDelegate {
    // Map setup
    private val center = LngLat(-102.981521, 35.742443)
    private val zoom = 1.0

    /** Controller driving the map and the bridge. */
    val controller: MTMapViewController = MTMapViewController(context).also { it.delegate = this }

    /** Options applied to the map. */
    val options: MTMapOptions = MTMapOptions(center, zoom)

    /** Style selection (default variant). */
    val referenceStyle: MTMapReferenceStyle = MTMapReferenceStyle.STREETS

    /** Optional callback for surfacing benchmark log lines to UI. */
    var onLog: ((String) -> Unit)? = null

    // Readiness gate to avoid style mutations before ON_READY
    private val readySignal = CompletableDeferred<Unit>()

    /** Set the MapTiler Cloud API key before rendering the map or running benchmarks. */
    fun setApiKey(key: String) {
        MTConfig.apiKey = key
    }

    /** Compose wrapper that renders the map using this benchmark's configuration. */
    @Suppress("FunctionName")
    @Composable
    fun Map(
        modifier: Modifier = Modifier,
        styleVariant: MTMapStyleVariant? = null,
    ) {
        MTMapView(
            referenceStyle = referenceStyle,
            options = options,
            controller = controller,
            modifier = modifier,
            styleVariant = styleVariant,
        )
    }

    // MTMapViewDelegate
    override fun onMapViewInitialized() {
        if (!readySignal.isCompleted) readySignal.complete(Unit)
        MTLogger.log("MapView initialized.", MTLogType.INFO)
        onLog?.invoke("MapView initialized.")
    }

    private val eventWaiters: MutableMap<MTEvent, MutableList<CompletableDeferred<Unit>>> = mutableMapOf()

    override fun onEventTriggered(
        event: MTEvent,
        data: MTData?,
    ) {
        synchronized(eventWaiters) {
            eventWaiters.remove(event)?.forEach { d -> if (!d.isCompleted) d.complete(Unit) }
        }
    }

    private suspend fun waitFor(
        event: MTEvent,
        timeoutMs: Long = 2000,
    ): Boolean {
        val deferred = CompletableDeferred<Unit>()
        synchronized(eventWaiters) {
            val list = eventWaiters.getOrPut(event) { mutableListOf() }
            list.add(deferred)
        }
        val res =
            withTimeoutOrNull(timeoutMs) {
                deferred.await()
                true
            }
        return res == true
    }

    /**
     * Kotlin-only micro-benchmarks
     * - Fibonacci(30) recursive
     * - Memory allocation of 100K UUID strings
     */
    suspend fun benchmarkKotlin() =
        withContext(Dispatchers.Default) {
            fun formatSeconds(seconds: Double): String = String.format(Locale.US, "%.6f", seconds)

            fun log(
                label: String,
                seconds: Double,
            ) {
                val msg = "Kotlin: $label elapsed ${formatSeconds(seconds)}"
                MTLogger.log(msg, MTLogType.INFO)
                onLog?.invoke(msg)
            }

            // Fibonacci(30)
            run {
                val start = System.nanoTime()

                fun fib(n: Int): Int = if (n < 2) n else fib(n - 1) + fib(n - 2)
                fib(30)
                val elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0
                log("Fibonacci 30", elapsedSeconds)
            }

            // Memory Allocation (100K UUID strings)
            run {
                val start = System.nanoTime()
                val arr = List(100_000) { java.util.UUID.randomUUID().toString() }
                // Prevent unused removal
                val size = arr.size
                val elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0
                log("Memory Allocation 100K (size=$size)", elapsedSeconds)
            }
        }

    /**
     * Simple JS bridge benchmarks using available Kotlin APIs.
     * Waits for ON_READY, then exercises:
     * - GetZoom (suspend getter; measures round-trip)
     * - ZoomIn + subsequent GetZoom (proxy for navigation command cost)
     * - AddSource + wait for isSourceLoaded
     * - AddLayer (fill) referencing the source
     */
    suspend fun benchmarkJS() {
        // Ensure the map/style is ready before mutating
        readySignal.await()

        fun formatSeconds(seconds: Double): String = String.format(Locale.US, "%.6f", seconds)

        fun log(
            label: String,
            seconds: Double,
        ) {
            val msg = "JS: $label elapsed ${formatSeconds(seconds)}"
            MTLogger.log(msg, MTLogType.INFO)
            onLog?.invoke(msg)
        }

        // ZoomIn
        run {
            val start = System.nanoTime()
            controller.zoomIn()
            waitFor(MTEvent.ON_IDLE)
            val elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0
            log("ZoomIn", elapsedSeconds)
        }

        // ZoomOut
        run {
            val start = System.nanoTime()
            controller.zoomOut()
            waitFor(MTEvent.ON_IDLE)
            val elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0
            log("ZoomOut", elapsedSeconds)
        }

        // GetPitch
        run {
            val start = System.nanoTime()
            val pitch = controller.getPitch()
            val elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0
            log("GetPitch (value=$pitch)", elapsedSeconds)
        }

        // SetPitch
        run {
            val start = System.nanoTime()
            controller.setPitch(2.0)
            waitFor(MTEvent.ON_IDLE)
            val elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0
            log("SetPitch", elapsedSeconds)
        }

        // Add a vector source and wait until it is loaded
        val style = controller.style
        if (style != null) {
            val apiKey = MTConfig.apiKey
            val url =
                URL(
                    "https://api.maptiler.com/tiles/v3/tiles.json" +
                        (if (apiKey.isNotEmpty()) "?key=$apiKey" else ""),
                )
            val sourceId = "planet-source"
            val source = MTVectorTileSource(sourceId, url)

            // Add source and wait until the style reports it as loaded
            run {
                val start = System.nanoTime()
                style.addSource(source)
                // Poll via suspend API to ensure the source became available
                // isSourceLoaded may return false immediately; wait until true with a few retries
                var waitedMsTotal = 0L
                var loaded = false
                repeat(20) {
                    loaded = style.isSourceLoaded(sourceId)
                    if (loaded) return@repeat
                    // Backoff ~50ms per try on background context
                    val step = 50L
                    waitedMsTotal += step
                    // Use coroutine-friendly delay
                    delay(step)
                }
                val elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0
                log("AddSource", elapsedSeconds)
            }

            // Add a simple fill layer referencing the source
            run {
                val start = System.nanoTime()
                val layerId = "planet-layer"
                val layer = MTFillLayer(layerId, sourceId)
                layer.color = Color.BLUE
                layer.sourceLayer = "default"
                try {
                    style.addLayer(layer)
                    // Await idle to approximate style application completion
                    waitFor(MTEvent.ON_IDLE)
                    val elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0
                    log("AddLayer", elapsedSeconds)
                } catch (e: Exception) {
                    MTLogger.log("AddLayer error: ${e.message}", MTLogType.ERROR)
                    onLog?.invoke("AddLayer error: ${e.message}")
                }
            }
        } else {
            MTLogger.log("Style is null; cannot run style-related benchmarks.", MTLogType.WARNING)
            onLog?.invoke("Style is null; skip style benchmarks.")
        }
    }
}
