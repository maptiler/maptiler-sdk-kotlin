/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.MTMapViewDelegate
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle

@Suppress("FunctionName")
@Composable
fun HomeScreen(
    navController: NavController,
    context: Context,
) {
    val mapController = MapController(context)

    val options = MTMapOptions()
    options.setMapTilerLogoIsVisible(true)

    Box(modifier = Modifier.fillMaxSize()) {
        MTMapView(
            MTMapReferenceStyle.STREETS,
            options,
            mapController.controller,
            modifier =
                Modifier
                    .fillMaxSize(),
        )

        ZoomControl(
            onZoomIn = { mapController.controller.zoomIn() },
            onZoomOut = { mapController.controller.zoomOut() },
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp),
        )
    }
}

class MapController(
    private val context: Context,
) : MTMapViewDelegate {
    val controller: MTMapViewController =
        MTMapViewController(context).apply {
            delegate = this@MapController
        }

    override fun onMapViewInitialized() {
        Log.i("Demo App", "Map View Initialized.")
    }
}
