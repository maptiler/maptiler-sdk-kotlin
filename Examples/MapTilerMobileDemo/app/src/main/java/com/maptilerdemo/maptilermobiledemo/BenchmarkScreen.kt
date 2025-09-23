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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { scope.launch { bench.benchmarkKotlin() } }) {
                        Text("Run Kotlin")
                    }
                    Button(onClick = { scope.launch { bench.benchmarkJS() } }) {
                        Text("Run JS")
                    }
                }
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier =
                        Modifier
                            .height(180.dp)
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
