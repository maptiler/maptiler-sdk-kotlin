/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextField
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.helpers.MTBenchmark
import kotlinx.coroutines.launch

@Composable
fun BenchmarkScreen(context: Context, navController: NavController) {
    val scope = rememberCoroutineScope()
    val logs = remember { mutableStateListOf<String>() }

    val bench = remember {
        MTBenchmark(context).apply {
            setApiKey(MTConfig.apiKey)
            onLog = { msg -> logs.add(msg) }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // No-op yet; controller is owned by bench and cleaned by Compose MTMapView
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map view
        bench.Map(Modifier.fillMaxSize())

        // Controls overlay
        Surface(
            color = Color(0x80000000),
            contentColor = Color.Black,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
        ) {
            CompositionLocalProvider(LocalContentColor provides Color.Black) {
                ProvideTextStyle(value = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)) {
                    Column(
                        modifier = Modifier.padding(12.dp).widthIn(min = 240.dp, max = 360.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                var markersOn by remember { mutableStateOf(true) }
                var showLogs by remember { mutableStateOf(true) }
                // Iterations dropdown
                val iterationOptions = listOf("100" to 100, "1K" to 1_000, "10K" to 10_000, "100K" to 100_000)
                var selectedIteration by remember { mutableStateOf(iterationOptions[1]) } // default 1K
                // Markers dropdown
                val markerOptions = listOf("100" to 100, "1K" to 1_000, "10K" to 10_000)
                var selectedMarker by remember { mutableStateOf(markerOptions[1]) } // default 1K
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { scope.launch { bench.benchmarkKotlin() } },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0), contentColor = Color.Black),
                    ) {
                        Text("Run Kotlin")
                    }
                    Button(
                        onClick = { scope.launch { bench.benchmarkJS() } },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0), contentColor = Color.Black),
                    ) {
                        Text("Run JS")
                    }
                    Button(
                        onClick = { showLogs = !showLogs },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0), contentColor = Color.Black),
                    ) {
                        Text(if (showLogs) "Hide Logs" else "Show Logs")
                    }
                }
                // Iterations selection
                IterationDropdown(
                    label = "Iterations",
                    selectedLabel = selectedIteration.first,
                    options = iterationOptions,
                    onSelect = { opt -> selectedIteration = opt },
                )
                // Markers selection + toggle
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Checkbox(checked = markersOn, onCheckedChange = { markersOn = it })
                    Text("Add markers", color = Color.Black)
                }
                IterationDropdown(
                    label = "Markers",
                    selectedLabel = selectedMarker.first,
                    options = markerOptions,
                    onSelect = { opt -> selectedMarker = opt },
                )
                Row {
                    Button(
                        onClick = {
                            val iters = selectedIteration.second
                            val markers = selectedMarker.second
                            scope.launch { bench.startStressTest(iterations = iters, markersAreOn = markersOn, markerCount = markers) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0), contentColor = Color.Black),
                    ) { Text("Start Stress Test") }
                }
                if (showLogs) {
                    Spacer(Modifier.height(8.dp))
                    Column(
                        modifier =
                            Modifier
                                .height(120.dp)
                                .widthIn(min = 240.dp, max = 360.dp)
                                .verticalScroll(rememberScrollState())
                                .background(Color(0xCCFFFFFF))
                                .padding(8.dp),
                    ) {
                        logs.forEach { line ->
                            Text(text = line, style = MaterialTheme.typography.bodySmall, color = Color.Black)
                        }
                    }
                }
                    }
                }
            }
        }
    }
}

@Composable
private fun IterationDropdown(
    label: String,
    selectedLabel: String,
    options: List<Pair<String, Int>>,
    onSelect: (Pair<String, Int>) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0), contentColor = Color.Black),
        ) {
            Text("$label: $selectedLabel")
        }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    options.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt.first, color = Color.Black) },
                            onClick = {
                                onSelect(opt)
                                expanded = false
                            },
                        )
                    }
                }
    }
}
