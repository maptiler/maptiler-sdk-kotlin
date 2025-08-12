/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.events.MTEvent
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.MTMapViewDelegate
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import com.maptiler.maptilersdk.map.style.layer.symbol.MTSymbolLayer
import com.maptiler.maptilersdk.map.types.MTData

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

        LayerControl(
            onSelect = { type: MTLayerType ->
                // TO DO: Update with proper source
                if (type == MTLayerType.SYMBOL) {
                    val mapTilerIcon = BitmapFactory.decodeResource(context.resources, R.drawable.maptiler_marker_icon)
                    val layer = MTSymbolLayer("symbolLayer", "symbolSource", mapTilerIcon)
                    mapController.controller.addLayer(layer)
                }
            },
            modifier =
                Modifier
                    .padding(5.dp),
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.TopEnd),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.End,
        ) {
            JumpControl(
                onJump = { coordinates: LngLat ->

                    val cameraOptions = MTCameraOptions(coordinates)
                    mapController.controller.jumpTo(cameraOptions)
                },
                modifier =
                    Modifier
                        .padding(5.dp),
            )

            ZoomControl(
                onZoomIn = { mapController.controller.zoomIn() },
                onZoomOut = { mapController.controller.zoomOut() },
                modifier =
                    Modifier
                        .padding(10.dp),
            )
        }

        NavigationControl(
            onFlyTo = {
                val unterageriCoordinates = LngLat(8.581651, 47.137765)
                val cameraOptions = MTCameraOptions(unterageriCoordinates)
                mapController.controller.flyTo(cameraOptions, null)
            },
            onEaseTo = {
                val brnoCoordinates = LngLat(16.626576, 49.212596)
                val cameraOptions = MTCameraOptions(brnoCoordinates)
                mapController.controller.easeTo(cameraOptions)
            },
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
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

        val unterageriCoordinates = LngLat(8.581651, 47.137765)
        val brnoCoordinates = LngLat(16.626576, 49.212596)

        val mapTilerIcon = BitmapFactory.decodeResource(context.resources, R.drawable.maptiler_marker_icon)
        val unterageriMarker = MTMarker(unterageriCoordinates, mapTilerIcon)
        controller.addMarker(unterageriMarker)

        val brnoPopup = MTTextPopup(brnoCoordinates, "Brno Development Hub")
        val brnoMarker = MTMarker(brnoCoordinates, brnoPopup)
        controller.addMarker(brnoMarker)
    }

    override fun onEventTriggered(
        event: MTEvent,
        data: MTData?,
    ) {
        Log.i("Demo App", "Map View Event Triggered: $event.")
    }
}
