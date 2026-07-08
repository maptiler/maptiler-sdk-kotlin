/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.offline.MTOfflineDownloadDelegate
import com.maptiler.maptilersdk.offline.MTOfflineError
import com.maptiler.maptilersdk.offline.MTOfflineManager
import com.maptiler.maptilersdk.offline.MTOfflinePackProgress
import com.maptiler.maptilersdk.offline.MTOfflinePackState
import com.maptiler.maptilersdk.offline.MTOfflineRegionDefinition
import com.maptiler.maptilersdk.offline.MTOfflineRegionGeometry
import com.maptiler.maptilersdk.offline.MTOfflineContext
import kotlinx.coroutines.launch

/**
 * Example demonstrating how to create an offline pack for a specific route (GeoJSON) and track its progress.
 */
class OfflineRouteExample : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                OfflineRouteScreen()
            }
        }
    }
}

@Composable
fun OfflineRouteScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var progress by remember { mutableStateOf(0f) }
    var statusText by remember { mutableStateOf("Ready to download route") }
    var isDownloading by remember { mutableStateOf(false) }

    // A simple sample GeoJSON LineString (e.g., a path in a park)
    val routeGeoJson =
        """
        {
            "type": "LineString",
            "coordinates": [
                [8.5417, 47.3769],
                [8.5427, 47.3779],
                [8.5437, 47.3789]
            ]
        }
        """.trimIndent()

    val downloadRoute: () -> Unit = {
        coroutineScope.launch {
            try {
                isDownloading = true
                statusText = "Creating offline pack..."

                // 1. Parse the route geometry
                val geometry = MTOfflineRegionGeometry.Route.fromGeoJson(routeGeoJson)

                // 2. Define the region
                val definition =
                    MTOfflineRegionDefinition(
                        geometry = geometry,
                        minZoom = 0,
                        maxZoom = 14,
                        referenceStyle = MTMapReferenceStyle.STREETS,
                        padding = 100.0, // 100 meters padding around the route
                    )

                // 3. Create the pack
                val pack = MTOfflineManager.createPack(context, definition)
                
                // 4. Set the delegate to observe progress
                pack.setDelegate(object : MTOfflineDownloadDelegate {
                    override fun offlinePack(packId: String, didChangeState: MTOfflinePackState) {
                        statusText = "State: ${didChangeState.name}"
                        if (didChangeState == MTOfflinePackState.COMPLETED) {
                            isDownloading = false
                            statusText = "Download completed!"
                            progress = 1f
                        } else if (didChangeState == MTOfflinePackState.FAILED || didChangeState == MTOfflinePackState.CANCELED) {
                            isDownloading = false
                            statusText = "Download failed or canceled."
                        }
                    }

                    override fun offlinePack(packId: String, didUpdateProgress: MTOfflinePackProgress) {
                        progress = didUpdateProgress.percentage.toFloat()
                        statusText = "Downloading: ${(progress * 100).toInt()}%"
                    }

                    override fun offlinePack(packId: String, didFailResource: MTOfflineError, context: MTOfflineContext) {
                        // Handle individual resource failures if needed
                    }

                    override fun offlinePack(packId: String, didSucceedResource: MTOfflineContext) {
                        // Handle individual resource successes if needed
                    }
                })

                pack.isProgressReportingEnabled = true

                // 5. Start the download
                statusText = "Starting download..."
                pack.download()

            } catch (e: Exception) {
                statusText = "Error: ${e.message}"
                isDownloading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = "Offline Route Downloader", style = MaterialTheme.typography.h6)
            Text(text = statusText)
            
            if (isDownloading) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = downloadRoute,
                enabled = !isDownloading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Download Route Pack")
            }
        }

        // We can show the map configured to the same style to verify later, or just a generic map
        MTMapView(
            modifier = Modifier.weight(1f),
            referenceStyle = MTMapReferenceStyle.STREETS
        ) { controller ->
            // Optionally, fit the camera to the route bounds when map is ready
            LaunchedEffect(Unit) {
               // ...
            }
        }
    }
}
