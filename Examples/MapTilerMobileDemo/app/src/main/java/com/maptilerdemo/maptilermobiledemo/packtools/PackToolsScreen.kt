/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo.packtools

import android.content.Context
import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.offline.MTBoundingBox
import com.maptiler.maptilersdk.offline.MTOfflineEstimator
import com.maptiler.maptilersdk.offline.MTOfflineGlyphRanges
import com.maptiler.maptilersdk.offline.MTOfflinePack
import com.maptiler.maptilersdk.offline.MTOfflinePackState
import com.maptiler.maptilersdk.offline.MTOfflineRegionDefinition
import com.maptiler.maptilersdk.offline.MTOfflineRegionGeometry
import com.maptiler.maptilersdk.offline.MTPackStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Hidden screen (long-press the Offline tab) with the pack estimator used by sales. Estimates use the bundled
// snapshot and size model and make no network requests; only the optional validation download is billed.

private enum class Area(val label: String) { VIEWPORT("Visible map"), GEOJSON("GeoJSON") }

private enum class Glyphs(val label: String) { DEFAULT("Latin/Greek/Cyrillic"), ALL("All scripts") }

private data class Result(
    val region: MTOfflineRegionDefinition,
    val stats: MTPackStats,
    val size: PackSizeEstimate?,
)

@Suppress("FunctionName")
@Composable
fun PackToolsScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val data = remember { PackToolsData.load(context) }
    val mapController = remember { MTMapViewController(context) }
    DisposableEffect(Unit) { onDispose { mapController.destroy() } }

    var styleKey by remember { mutableStateOf("streets/default") }
    var styleMenu by remember { mutableStateOf(false) }
    var area by remember { mutableStateOf(Area.VIEWPORT) }
    var geoJson by remember { mutableStateOf("") }
    var minZoom by remember { mutableIntStateOf(0) }
    var maxZoom by remember { mutableIntStateOf(14) }
    var padding by remember { mutableStateOf("") }
    var terrain by remember { mutableStateOf(false) }
    var glyphs by remember { mutableStateOf(Glyphs.DEFAULT) }

    var result by remember { mutableStateOf<Result?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var confirmValidation by remember { mutableStateOf(false) }
    var validating by remember { mutableStateOf(false) }
    var validationProgress by remember { mutableStateOf(0f) }
    var measurement by remember { mutableStateOf<PackMeasurement?>(null) }

    fun estimate() {
        scope.launch {
            result = null
            measurement = null
            message = null
            val metadata = data.snapshot.styles[styleKey]
            val style = PackStyle.withId(styleKey)
            if (metadata == null || style == null) {
                message = "No snapshot data for $styleKey."
                return@launch
            }
            try {
                val geometry =
                    when (area) {
                        Area.VIEWPORT -> {
                            val bounds = mapController.getBounds()
                            MTOfflineRegionGeometry.BoundingBox(
                                MTBoundingBox(bounds.southwest.lng, bounds.southwest.lat, bounds.northeast.lng, bounds.northeast.lat),
                            )
                        }
                        Area.GEOJSON ->
                            if (geoJson.contains("Polygon")) {
                                MTOfflineRegionGeometry.Polygon.fromGeoJson(geoJson)
                            } else {
                                MTOfflineRegionGeometry.Route.fromGeoJson(geoJson)
                            }
                    }
                val region =
                    MTOfflineRegionDefinition(
                        geometry = geometry,
                        minZoom = minZoom,
                        maxZoom = maxZoom,
                        referenceStyle = style.style,
                        styleVariant = style.variant,
                        padding = padding.toDoubleOrNull(),
                        isTerrainEnabled = terrain,
                        glyphRanges = if (glyphs == Glyphs.ALL) MTOfflineGlyphRanges.All else MTOfflineGlyphRanges.LatinGreekCyrillic,
                    )
                result =
                    withContext(Dispatchers.Default) {
                        // No network requests: the metadata comes from the bundled snapshot.
                        val stats = MTOfflineEstimator().estimatePack(region, metadata)
                        val size = data.model?.let { PackSizeEstimator.estimate(region, metadata, stats, it, data.population) }
                        Result(region, stats, size)
                    }
                if (data.model == null) message = "No size model bundled: sizes are not calibrated."
            } catch (error: Exception) {
                message = "Estimate failed: ${error.message ?: error}"
            }
        }
    }

    fun validate(current: Result) {
        scope.launch {
            validating = true
            validationProgress = 0f
            try {
                val pack = MTOfflinePack.createPack(context, current.region)
                val progressJob =
                    launch {
                        pack.progressFlow.collect { validationProgress = it.percentage.toFloat() }
                    }
                runCatching { pack.download() }
                progressJob.cancel()
                measurement = withContext(Dispatchers.IO) { PackMeasurement.measure(context, pack.id) }
                if (pack.state != MTOfflinePackState.COMPLETED) message = "Download ended as ${pack.state}"
                runCatching { pack.remove() }
            } catch (error: Exception) {
                message = "Validation failed: ${error.message ?: error}"
            }
            validating = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Pack tools", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            TextButton(onClick = onClose) { Text("Close") }
        }

        if (area == Area.VIEWPORT) {
            MTMapView(
                referenceStyle = MTMapReferenceStyle.STREETS,
                options = MTMapOptions(),
                controller = mapController,
                modifier = Modifier.fillMaxWidth().height(240.dp),
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Area.entries.forEach { FilterChip(selected = area == it, onClick = { area = it }, label = { Text(it.label) }) }
            }
            if (area == Area.GEOJSON) {
                OutlinedTextField(
                    value = geoJson,
                    onValueChange = { geoJson = it },
                    label = { Text("Polygon or LineString GeoJSON") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                )
            }

            Column {
                OutlinedButton(onClick = { styleMenu = true }, modifier = Modifier.fillMaxWidth()) { Text("Style: $styleKey") }
                DropdownMenu(expanded = styleMenu, onDismissRequest = { styleMenu = false }) {
                    data.snapshot.styles.keys.sorted().forEach { key ->
                        DropdownMenuItem(text = { Text(key) }, onClick = {
                            styleKey = key
                            styleMenu = false
                        })
                    }
                }
            }

            ZoomStepper("Min zoom", minZoom, 0..maxZoom) { minZoom = it }
            ZoomStepper("Max zoom", maxZoom, minZoom..22) { maxZoom = it }
            OutlinedTextField(
                value = padding,
                onValueChange = { padding = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Padding in meters (default)") },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("3D terrain", modifier = Modifier.weight(1f))
                Switch(checked = terrain, onCheckedChange = { terrain = it })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Glyphs.entries.forEach { FilterChip(selected = glyphs == it, onClick = { glyphs = it }, label = { Text(it.label) }) }
            }
            Button(onClick = { estimate() }, modifier = Modifier.fillMaxWidth()) { Text("Estimate") }

            message?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            result?.let { current ->
                HorizontalDivider()
                StatsSection(context, current)
                HorizontalDivider()
                Text("Validate (billed)", fontWeight = FontWeight.Bold)
                OutlinedButton(
                    onClick = { confirmValidation = true },
                    enabled = !validating && !current.stats.exceedsLimit,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Download and measure (${current.stats.resourceCount} requests)") }
                if (validating) LinearProgressIndicator(progress = { validationProgress }, modifier = Modifier.fillMaxWidth())
                measurement?.let { ValidationSection(context, it, current.size) }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (confirmValidation) {
        val current = result
        AlertDialog(
            onDismissRequest = { confirmValidation = false },
            title = { Text("Download this pack?") },
            text = { Text("This makes ${current?.stats?.resourceCount ?: 0} billed requests, then deletes the pack.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmValidation = false
                    current?.let { validate(it) }
                }) { Text("Download") }
            },
            dismissButton = { TextButton(onClick = { confirmValidation = false }) { Text("Cancel") } },
        )
    }
}

@Suppress("FunctionName")
@Composable
private fun ZoomStepper(label: String, value: Int, range: IntRange, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label: $value", modifier = Modifier.weight(1f))
        OutlinedButton(onClick = { onChange((value - 1).coerceIn(range)) }) { Text("−") }
        Spacer(Modifier.padding(4.dp))
        OutlinedButton(onClick = { onChange((value + 1).coerceIn(range)) }) { Text("+") }
    }
}

@Suppress("FunctionName")
@Composable
private fun Line(label: String, value: String, color: Color = Color.Unspecified) {
    Row {
        Text(label, modifier = Modifier.weight(1f))
        Text(value, color = color, fontWeight = FontWeight.Medium)
    }
}

private fun bytes(context: Context, value: Double) = Formatter.formatFileSize(context, value.toLong())

@Suppress("FunctionName")
@Composable
private fun StatsSection(context: Context, result: Result) {
    val stats = result.stats
    Text("Requests", fontWeight = FontWeight.Bold)
    Line(
        "Resources",
        "%,d / %,d".format(stats.totalResourceCount, stats.resourceLimit),
        if (stats.exceedsLimit) MaterialTheme.colorScheme.error else Color.Unspecified,
    )
    stats.minimumPackCount?.let { Line("Packs needed (at least)", it.toString()) }
    Line("Glyphs", "%,d".format(stats.glyphCount))
    Line("Sprites + style", "%,d".format(stats.spriteCount + stats.styleCount))
    stats.sources.forEach { Line("${it.id} z${it.minZoom}–${it.maxZoom}", "%,d".format(it.tileCount)) }

    val size = result.size ?: return
    Text("Size", fontWeight = FontWeight.Bold)
    listOf("Download" to size.range { it.wire }, "On device" to size.range { it.allocated }).forEach { (label, range) ->
        Line(label, "${bytes(context, range.second)}  (${bytes(context, range.first)} – ${bytes(context, range.third)})")
    }
    Line("Map tiles", bytes(context, size.tiles.allocated))
    Line("Style, sprites, glyphs", bytes(context, size.overhead.allocated))
    val notes =
        listOfNotNull(
            size.uncalibratedSources.takeIf { it.isNotEmpty() }?.let { "Rough: no calibration for ${it.joinToString()}" },
            size.extrapolatedSources.takeIf { it.isNotEmpty() }?.let { "Extrapolated beyond calibrated zoom: ${it.joinToString()}" },
            size.unmeasuredOverhead.takeIf { it.isNotEmpty() }?.let { "Rough: unmeasured ${it.joinToString()}" },
        )
    notes.forEach { Text(it, fontSize = 12.sp, color = Color(0xFFB45309)) }
}

@Suppress("FunctionName")
@Composable
private fun ValidationSection(context: Context, measurement: PackMeasurement, estimate: PackSizeEstimate?) {
    fun error(actual: Double, estimated: Double?) =
        if (estimated == null || actual <= 0) "" else " (estimate %+.1f%%)".format((estimated - actual) / actual * 100)
    Line("Tile files", "%,d".format(measurement.tileFiles))
    Line("Download, actual", bytes(context, measurement.total.wire) + error(measurement.total.wire, estimate?.total?.wire))
    Line("On device, actual", bytes(context, measurement.total.allocated) + error(measurement.total.allocated, estimate?.total?.allocated))
}
