/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.offline.MTBoundingBox
import com.maptiler.maptilersdk.offline.MTOfflineManager
import com.maptiler.maptilersdk.offline.MTOfflinePack
import com.maptiler.maptilersdk.offline.MTOfflinePackState
import com.maptiler.maptilersdk.offline.MTOfflineRegionDefinition
import com.maptiler.maptilersdk.offline.MTOfflineRegionGeometry
import kotlinx.coroutines.launch

class BasicOfflineDownloadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                BasicOfflineDownloadScreen()
            }
        }
    }
}

@Composable
fun BasicOfflineDownloadScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var activePack by remember { mutableStateOf<MTOfflinePack?>(null) }
    var packState by remember { mutableStateOf(MTOfflinePackState.PENDING) }
    var downloadProgress by remember { mutableStateOf(0f) }
    
    val mapController = remember { MTMapViewController(context) }

    // Observe pack state and progress when activePack changes
    LaunchedEffect(activePack) {
        activePack?.let { pack ->
            launch {
                pack.stateFlow.collect { state ->
                    packState = state
                }
            }
            launch {
                pack.progressFlow.collect { progress ->
                    downloadProgress = progress.percentage.toFloat()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Display the map
        MTMapView(
            modifier = Modifier.fillMaxSize(),
            referenceStyle = MTMapReferenceStyle.STREETS,
            controller = mapController,
            options = MTMapOptions(
                zoom = 12.0,
                center = com.maptiler.maptilersdk.map.LngLat(8.5417, 47.3769)
            )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .background(MaterialTheme.colors.surface.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Basic Offline Download", style = MaterialTheme.typography.h6)
            Text(text = "State: ${packState.name}")

            // Show progress bar only while actively downloading
            if (packState == MTOfflinePackState.DOWNLOADING) {
                LinearProgressIndicator(
                    progress = downloadProgress,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val bounds = mapController.getBounds()
                                
                                // Define the region using the current map bounds
                                val geometry = MTOfflineRegionGeometry.BoundingBox(MTBoundingBox.fromBounds(bounds))
                                val definition = MTOfflineRegionDefinition(
                                    geometry = geometry,
                                    minZoom = 0,
                                    maxZoom = 14,
                                    referenceStyle = MTMapReferenceStyle.STREETS
                                )
                                
                                // Create the offline pack
                                val pack = MTOfflineManager.createPack(context, definition)
                                activePack = pack
                                
                                // Start the download process
                                pack.download()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    enabled = packState != MTOfflinePackState.DOWNLOADING
                ) {
                    Text("Download View")
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            activePack?.let { pack ->
                                try {
                                    mapController.loadOfflinePack(pack)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    },
                    enabled = packState == MTOfflinePackState.COMPLETED
                ) {
                    Text("Load Pack")
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                // Cancel any active download and remove the pack from the device's storage
                                activePack?.remove()
                                activePack = null
                                packState = MTOfflinePackState.PENDING
                                downloadProgress = 0f
                                
                                // Restore the map to an online style
                                mapController.unloadOfflinePack(MTMapReferenceStyle.STREETS)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    enabled = activePack != null && packState != MTOfflinePackState.DOWNLOADING
                ) {
                    Text("Clear")
                }
            }
        }
    }
}
