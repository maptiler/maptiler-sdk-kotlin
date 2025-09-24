/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
import com.maptiler.maptilersdk.map.style.MTTileScheme
import com.maptiler.maptilersdk.map.style.layer.fill.MTFillLayer
import com.maptiler.maptilersdk.map.style.source.MTGeoJSONSource
import com.maptiler.maptilersdk.map.style.source.MTSourceType
import com.maptiler.maptilersdk.map.style.source.MTVectorTileSource
import com.maptiler.maptilersdk.map.types.MTData
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.net.URL
import java.util.Locale
import kotlin.random.Random

/**
 * Basic benchmarking helper for Kotlin SDK.
 */
class MTBenchmark(
    context: Context,
) : MTMapViewDelegate {
    private companion object {
        private const val ARCGIS_TILE_TEMPLATE =
            "https://docs.maptiler.com/sdk-js/assets/earthquakes.geojson"
    }

    // Map setup
    private val center = LngLat(-102.981521, 35.742443)
    private val zoom = 1.0

    val controller: MTMapViewController = MTMapViewController(context).also { it.delegate = this }

    val options: MTMapOptions = MTMapOptions(center, zoom)

    val referenceStyle: MTMapReferenceStyle = MTMapReferenceStyle.STREETS

    var onLog: ((String) -> Unit)? = null

    // Readiness gate to avoid style mutations before ON_READY
    private val readySignal = CompletableDeferred<Unit>()

    // Startup timing
    private var initStartNs: Long? = null
    private var mapInitEndNs: Long? = null
    private var readyEndNs: Long? = null

    // FPS tracking removed per request; only startup timings remain

    fun setApiKey(key: String) {
        MTConfig.apiKey = key
    }

    @Suppress("FunctionName")
    @Composable
    fun Map(
        modifier: Modifier = Modifier,
        styleVariant: MTMapStyleVariant? = null,
    ) {
        // Record the moment we first add the map view to the UI
        if (initStartNs == null) {
            initStartNs = System.nanoTime()
            onLog?.invoke("t: $initStartNs - MapViewInitStarted")
        }
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
        val now = System.nanoTime()
        MTLogger.log("MapView initialized.", MTLogType.INFO)
        onLog?.invoke("MapView initialized.")
        mapInitEndNs = now
        initStartNs?.let { start ->
            val elapsedSec = (now - start) / 1_000_000_000.0
            onLog?.invoke("t: $now - MapViewInitEnded")
            onLog?.invoke("t: ${"%.6f".format(Locale.US, elapsedSec)} s - MapViewInitElapsed")
        }
    }

    private val eventWaiters: MutableMap<MTEvent, MutableList<CompletableDeferred<Unit>>> = mutableMapOf()

    override fun onEventTriggered(
        event: MTEvent,
        data: MTData?,
    ) {
        synchronized(eventWaiters) {
            eventWaiters.remove(event)?.forEach { d -> if (!d.isCompleted) d.complete(Unit) }
        }

        // Startup milestones
        when (event) {
            MTEvent.ON_READY -> {
                val now = System.nanoTime()
                readyEndNs = now
                initStartNs?.let { start ->
                    val elapsedSec = (now - start) / 1_000_000_000.0
                    onLog?.invoke("t: $now - MapReady")
                    onLog?.invoke("t: ${"%.6f".format(Locale.US, elapsedSec)} s - MapReadyElapsed")
                }
            }
            else -> Unit
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

        // ZoomIn (measure until animation starts, not until idle)
        run {
            val start = System.nanoTime()
            controller.zoomIn()
            waitFor(MTEvent.ON_ZOOM_START)
            val elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0
            log("ZoomIn", elapsedSeconds)
        }

        // ZoomOut (measure until animation starts, not until idle)
        run {
            val start = System.nanoTime()
            controller.zoomOut()
            waitFor(MTEvent.ON_ZOOM_START)
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

        // SetPitch (measure until pitch update begins)
        run {
            val start = System.nanoTime()
            controller.setPitch(2.0)
            waitFor(MTEvent.ON_PITCH_UPDATE_START)
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

    suspend fun startStressTest(
        iterations: Int,
        markersAreOn: Boolean,
        markerCount: Int,
    ) {
        readySignal.await()

        val totalStart = System.nanoTime()

        stressNavigation(iterations)

        if (markersAreOn) {
            stressMarkersRandomDistribution(markerCount)
        }

        stressSourcesAndLayers(iterations)

        stressRealTime()

        val totalElapsedSec = (System.nanoTime() - totalStart) / 1_000_000_000.0
        onLog?.invoke(">>> TOTAL Benchmark elapsed: ${"%.6f".format(Locale.US, totalElapsedSec)}")
    }

    private suspend fun stressNavigation(iterations: Int) {
        stressZoom(iterations)
        stressJumpTo(iterations)
        stressFlyTo(iterations)
    }

    private suspend fun stressZoom(iterations: Int) {
        val elapsed = mutableListOf<Double>()
        val totalStart = System.nanoTime()

        for (i in 0..iterations) {
            val start = System.nanoTime()
            if (i % 2 == 0) controller.zoomIn() else controller.zoomOut()
            waitFor(MTEvent.ON_ZOOM_START, timeoutMs = 1_000)
            val perSec = (System.nanoTime() - start) / 1_000_000_000.0
            elapsed.add(perSec)
        }

        val totalElapsed = (System.nanoTime() - totalStart) / 1_000_000_000.0
        val avg = if (elapsed.isNotEmpty()) elapsed.sum() / elapsed.size else 0.0
        onLog?.invoke("total t: ${"%.6f".format(Locale.US, totalElapsed)} - Zoom")
        onLog?.invoke("avg t: ${"%.6f".format(Locale.US, avg)} - Zoom")
        onLog?.invoke(">>> ZOOM Benchmark elapsed: ${"%.6f".format(Locale.US, totalElapsed)}")
    }

    private suspend fun stressJumpTo(iterations: Int) {
        val elapsed = mutableListOf<Double>()
        val totalStart = System.nanoTime()

        for (i in 0..iterations) {
            val start = System.nanoTime()
            val even = i % 2 == 0
            controller.jumpTo(
                com.maptiler.maptilersdk.map.options.MTCameraOptions(
                    center = LngLat(if (even) 19.567 else 45.567, if (!even) 19.567 else 45.567),
                ),
            )
            val perSec = (System.nanoTime() - start) / 1_000_000_000.0
            elapsed.add(perSec)
        }

        val totalElapsed = (System.nanoTime() - totalStart) / 1_000_000_000.0
        val avg = if (elapsed.isNotEmpty()) elapsed.sum() / elapsed.size else 0.0
        onLog?.invoke("total t: ${"%.6f".format(Locale.US, totalElapsed)} - JumpTo")
        onLog?.invoke("avg t: ${"%.6f".format(Locale.US, avg)} - JumpTo")
        onLog?.invoke(">>> JUMPTO Benchmark elapsed: ${"%.6f".format(Locale.US, totalElapsed)}")
    }

    private suspend fun stressFlyTo(iterations: Int) {
        val elapsed = mutableListOf<Double>()
        val totalStart = System.nanoTime()

        for (i in 0..iterations) {
            val start = System.nanoTime()
            val even = i % 2 == 0
            controller.flyTo(
                com.maptiler.maptilersdk.map.options.MTCameraOptions(
                    center = LngLat(if (even) 19.567 else 45.567, if (!even) 19.567 else 45.567),
                ),
                null,
            )
            val perSec = (System.nanoTime() - start) / 1_000_000_000.0
            elapsed.add(perSec)
        }

        val totalElapsed = (System.nanoTime() - totalStart) / 1_000_000_000.0
        val avg = if (elapsed.isNotEmpty()) elapsed.sum() / elapsed.size else 0.0
        onLog?.invoke("total t: ${"%.6f".format(Locale.US, totalElapsed)} - FlyTo")
        onLog?.invoke("avg t: ${"%.6f".format(Locale.US, avg)} - FlyTo")
        onLog?.invoke(">>> FLYTO Benchmark elapsed: ${"%.6f".format(Locale.US, totalElapsed)}")
    }

    private suspend fun stressMarkersRandomDistribution(iterations: Int) {
        val style = controller.style
        if (style == null) {
            onLog?.invoke("Style is null; cannot add markers.")
            return
        }

        val elapsed = mutableListOf<Double>()
        val totalStart = System.nanoTime()

        for (i in 0..iterations) {
            val start = System.nanoTime()

            val lat = Random.nextDouble(-90.0, 90.0)
            val lng = Random.nextDouble(-180.0, 180.0)
            val marker = com.maptiler.maptilersdk.annotations.MTMarker(LngLat(lng, lat))

            style.addMarker(marker)

            val perSec = (System.nanoTime() - start) / 1_000_000_000.0
            elapsed.add(perSec)
        }

        val totalElapsed = (System.nanoTime() - totalStart) / 1_000_000_000.0
        val avg = if (elapsed.isNotEmpty()) elapsed.sum() / elapsed.size else 0.0
        onLog?.invoke("total t: ${"%.6f".format(Locale.US, totalElapsed)} - AddMarker")
        onLog?.invoke("avg t: ${"%.6f".format(Locale.US, avg)} - AddMarker")
        onLog?.invoke(">>> MARKERS RND DIST Benchmark elapsed: ${"%.6f".format(Locale.US, totalElapsed)}")
    }

    private suspend fun stressSourcesAndLayers(iterations: Int) {
        val style = controller.style
        if (style == null) {
            onLog?.invoke("Style is null; cannot add sources/layers.")
            return
        }
        stressMultipleSourceAndMultipleLayers(iterations)
    }

    private suspend fun stressMultipleSourceAndMultipleLayers(iterations: Int) {
        val style = controller.style
        if (style == null) {
            onLog?.invoke("Style is null; cannot add sources/layers.")
            return
        }

        fun withAlpha(
            color: Int,
            alpha: Float,
        ): Int {
            val a = (alpha.coerceIn(0f, 1f) * 255).toInt()
            return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color))
        }

        fun randomColor(): Int {
            val r = Random.nextInt(0, 256)
            val g = Random.nextInt(0, 256)
            val b = Random.nextInt(0, 256)
            return Color.rgb(r, g, b)
        }

        val key = MTConfig.apiKey.takeIf { it.isNotEmpty() }?.let { "?key=$it" } ?: ""

        data class Src(val sourceLayer: String, val url: String, val color: Int)

        val alpha02 = 0.2f
        val sources =
            listOf(
                Src("contour", "https://api.maptiler.com/tiles/contours-v2/{z}/{x}/{y}.pbf$key", withAlpha(Color.RED, alpha02)),
                Src("administrative", "https://api.maptiler.com/tiles/countries/{z}/{x}/{y}.pbf$key", withAlpha(Color.WHITE, alpha02)),
                Src("land", "https://api.maptiler.com/tiles/land/{z}/{x}/{y}.pbf$key", withAlpha(Color.BLUE, alpha02)),
                Src("contour", "https://api.maptiler.com/tiles/ocean/{z}/{x}/{y}.pbf$key", withAlpha(Color.GREEN, alpha02)),
                Src("ski", "https://api.maptiler.com/tiles/outdoor/{z}/{x}/{y}.pbf$key", withAlpha(Color.parseColor("#FFA500"), alpha02)),
                Src("batiments", "https://api.maptiler.com/tiles/fr-cadastre/{z}/{x}/{y}.pbf$key", withAlpha(Color.BLACK, alpha02)),
                Src("building", "https://api.maptiler.com/tiles/ch-swisstopo-lbm/{z}/{x}/{y}.pbf$key", withAlpha(Color.LTGRAY, alpha02)),
            )

        val openMapTilesLayers =
            listOf(
                "water",
                "waterway",
                "landcover",
                "landuse",
                "mountain_peak",
                "park",
                "boundary",
                "aeroway",
                "transportation",
                "building",
                "water_name",
                "transportation_name",
                "place",
                "housenumber",
                "poi",
                "aerodrome_label",
            ).map { it to randomColor() }

        val openZoomStackLayers =
            listOf(
                "sea",
                "names",
                "rail",
                "waterlines",
                "etl",
                "foreshore",
                "sites",
                "railwaystations",
                "roads",
                "greenspaces",
                "contours",
                "buildings",
                "boundaries",
                "airports",
                "woodland",
                "national_parks",
                "urban_areas",
                "surfacewater",
            ).map { it to randomColor() }

        val elapsed = mutableListOf<Double>()
        val totalStart = System.nanoTime()

        // First batch: individual sources with different URL templates and alpha colors
        for (s in sources) {
            val start = System.nanoTime()
            val srcId = "source-${s.url}"
            try {
                style.addSource(MTVectorTileSource(srcId, URL(s.url)))
                val layer = MTFillLayer("layer-${s.url}", srcId)
                layer.color = s.color
                layer.sourceLayer = s.sourceLayer
                style.addLayer(layer)
            } catch (e: Exception) {
                // Keep going in stress runs
                onLog?.invoke("MSML: error adding ${s.sourceLayer} -> ${e.message}")
            }
            val per = (System.nanoTime() - start) / 1_000_000_000.0
            elapsed.add(per)
        }

        // OpenMapTiles batch (single URL template, many layers)
        for ((layerName, color) in openMapTilesLayers) {
            val start = System.nanoTime()
            val srcId = "source-$layerName"
            try {
                val url = URL("https://api.maptiler.com/tiles/v3-openmaptiles/{z}/{x}/{y}.pbf$key")
                style.addSource(MTVectorTileSource(srcId, url))
                val layer = MTFillLayer("layer-$layerName", srcId)
                layer.color = color
                layer.sourceLayer = layerName
                style.addLayer(layer)
            } catch (_: Exception) {
                // continue
            }
            val per = (System.nanoTime() - start) / 1_000_000_000.0
            elapsed.add(per)
        }

        // OpenZoomStack batch (single URL template, many layers)
        for ((layerName, color) in openZoomStackLayers) {
            val start = System.nanoTime()
            val srcId = "source-$layerName"
            try {
                val url = URL("https://api.maptiler.com/tiles/uk-openzoomstack/{z}/{x}/{y}.pbf$key")
                style.addSource(MTVectorTileSource(srcId, url))
                val layer = MTFillLayer("layer-$layerName", srcId)
                layer.color = color
                layer.sourceLayer = layerName
                style.addLayer(layer)
            } catch (_: Exception) {
                // continue
            }
            val per = (System.nanoTime() - start) / 1_000_000_000.0
            elapsed.add(per)
        }

        val totalElapsed = (System.nanoTime() - totalStart) / 1_000_000_000.0
        val avg = if (elapsed.isNotEmpty()) elapsed.sum() / elapsed.size else 0.0
        onLog?.invoke("total t: ${"%.6f".format(Locale.US, totalElapsed)} - SourcesAndLayers")
        onLog?.invoke("avg t: ${"%.6f".format(Locale.US, avg)} - SourcesAndLayers")
        onLog?.invoke(">>> SOURCES AND LAYERS Benchmark elapsed: ${"%.6f".format(Locale.US, totalElapsed)}")
    }

    suspend fun runMultipleSourceAndMultipleLayers() {
        readySignal.await()
        stressMultipleSourceAndMultipleLayers(0)
    }

    suspend fun benchmarkSingleSourceMultipleLayers(
        tileTemplate: String = ARCGIS_TILE_TEMPLATE,
        sourceLayerName: String = "Parcels",
    ) {
        readySignal.await()
        singleSourceMultipleLayers(2, tileTemplate, sourceLayerName)
        singleSourceMultipleLayers(100, tileTemplate, sourceLayerName)
        singleSourceMultipleLayers(1_000, tileTemplate, sourceLayerName)
    }

    suspend fun runSingleSourceMultipleLayers(
        count: Int,
        tileTemplate: String = ARCGIS_TILE_TEMPLATE,
        sourceLayerName: String = "Parcels",
    ) {
        readySignal.await()
        singleSourceMultipleLayers(count, tileTemplate, sourceLayerName)
    }

    private suspend fun singleSourceMultipleLayers(
        count: Int,
        tileTemplate: String,
        sourceLayerName: String,
    ) {
        val style = controller.style
        if (style == null) {
            onLog?.invoke("Style is null; cannot run Single Source / Multiple Layers ($count)")
            return
        }

        val sourceId = "ssml-source-${System.currentTimeMillis()}"
        val src =
            MTVectorTileSource(
                identifier = sourceId,
                attribution = null,
                bounds = doubleArrayOf(-180.0, -85.051129, 180.0, 85.051129),
                maxZoom = 22.0,
                minZoom = 0.0,
                tiles = arrayOf(URL(tileTemplate)),
                url = null,
                type = MTSourceType.VECTOR,
                scheme = MTTileScheme.XYZ,
            )

        try {
            style.addSource(src)
            onLog?.invoke("SSML: addSource('$sourceId') issued")
        } catch (e: Exception) {
            onLog?.invoke("SSML: addSource error: ${e.message}")
        }

        // Wait briefly for the source to become loaded
        run {
            var loaded = false
            var attempts = 0
            repeat(40) {
                attempts++
                loaded = style.isSourceLoaded(sourceId)
                if (loaded) return@repeat
                delay(50)
            }
            onLog?.invoke("SSML: isSourceLoaded('$sourceId') after $attempts attempts => $loaded")
        }

        val times = mutableListOf<Double>()
        val totalStart = System.nanoTime()

        for (i in 0 until count) {
            val start = System.nanoTime()
            val layer = MTFillLayer("ssml-layer-$i", sourceId)
            layer.color = if (i % 2 == 0) Color.BLUE else Color.RED
            layer.sourceLayer = sourceLayerName
            try {
                style.addLayer(layer)
            } catch (e: Exception) {
                onLog?.invoke("SSML: addLayer('${layer.identifier}') error: ${e.message}")
            }
            val per = (System.nanoTime() - start) / 1_000_000_000.0
            times.add(per)
        }

        val total = (System.nanoTime() - totalStart) / 1_000_000_000.0
        val avg = if (times.isNotEmpty()) times.sum() / times.size else 0.0
        onLog?.invoke("total t: ${"%.6f".format(Locale.US, total)} - SingleSourceAndLayers($count)")
        onLog?.invoke("avg t: ${"%.6f".format(Locale.US, avg)} - SingleSourceAndLayers($count)")
        onLog?.invoke(">>> SINGLE SOURCE + MULTIPLE LAYERS ($count) Benchmark elapsed: ${"%.6f".format(Locale.US, total)}")
    }

    private suspend fun stressRealTime() {
        val style = controller.style ?: return

        val start = System.nanoTime()

        val source =
            MTGeoJSONSource.fromUrl(
                identifier = "realtimesource",
                url = URL("https://docs.maptiler.com/sdk-js/assets/earthquakes.geojson"),
            )
        style.addSource(source)

        val layer =
            com.maptiler.maptilersdk.map.style.layer.symbol.MTSymbolLayer(
                identifier = "realtimelayer",
                sourceIdentifier = source.identifier,
            )
        style.addLayer(layer)

        var count = 0
        while (count < 31) {
            val url =
                if (count % 2 == 0) {
                    URL("https://docs.maptiler.com/sdk-js/assets/earthquakes.geojson")
                } else {
                    URL("https://docs.maptiler.com/sdk-js/assets/us_states.geojson")
                }
            source.setData(url, controller)
            delay(1_000)
            count += 1
        }

        val elapsedSec = (System.nanoTime() - start) / 1_000_000_000.0
        onLog?.invoke(">>> RealTime Test elapsed: ${"%.6f".format(Locale.US, elapsedSec)}")
    }

    suspend fun runGeoJSONSingleSourceMultipleSymbolLayers(
        count: Int,
        dataUrl: String = "https://docs.maptiler.com/sdk-js/assets/earthquakes.geojson",
    ) {
        readySignal.await()
        geojsonSingleSourceMultipleSymbolLayers(count, dataUrl)
    }

    private fun makeDotIcon(color: Int = Color.RED): Bitmap {
        val size = 24
        val radius = 8f
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        canvas.drawCircle(size / 2f, size / 2f, radius, paint)
        return bmp
    }

    private suspend fun geojsonSingleSourceMultipleSymbolLayers(
        count: Int,
        dataUrl: String,
    ) {
        val style = controller.style
        if (style == null) {
            onLog?.invoke("Style is null; cannot run GeoJSON SSML ($count)")
            return
        }

        val sourceId = "geojson-ssml-source-${System.currentTimeMillis()}"
        val src = MTGeoJSONSource.fromUrl(sourceId, URL(dataUrl))

        try {
            style.addSource(src)
            onLog?.invoke("GJ-SSML: addSource('$sourceId') issued")
        } catch (e: Exception) {
            onLog?.invoke("GJ-SSML: addSource error: ${e.message}")
        }

        // Small wait to let data become available
        run {
            repeat(10) { delay(50) }
        }

        val times = mutableListOf<Double>()
        val totalStart = System.nanoTime()

        val iconA = makeDotIcon(Color.RED)
        val iconB = makeDotIcon(Color.BLUE)

        for (i in 0 until count) {
            val start = System.nanoTime()
            val layer =
                com.maptiler.maptilersdk.map.style.layer.symbol.MTSymbolLayer(
                    identifier = "geojson-ssml-layer-$i",
                    sourceIdentifier = sourceId,
                    icon = if (i % 2 == 0) iconA else iconB,
                )
            try {
                style.addLayer(layer)
            } catch (e: Exception) {
                onLog?.invoke("GJ-SSML: addLayer('${layer.identifier}') error: ${e.message}")
            }
            val per = (System.nanoTime() - start) / 1_000_000_000.0
            times.add(per)
        }

        val total = (System.nanoTime() - totalStart) / 1_000_000_000.0
        val avg = if (times.isNotEmpty()) times.sum() / times.size else 0.0
        onLog?.invoke("total t: ${"%.6f".format(Locale.US, total)} - GeoJSON SingleSource+SymbolLayers($count)")
        onLog?.invoke("avg t: ${"%.6f".format(Locale.US, avg)} - GeoJSON SingleSource+SymbolLayers($count)")
        onLog?.invoke(">>> GEOJSON SINGLE SOURCE + SYMBOL LAYERS ($count) Benchmark elapsed: ${"%.6f".format(Locale.US, total)}")
    }
}
