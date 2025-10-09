package com.maptiler.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.MTMapViewDelegate
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.types.MTData

/**
 * Compose example that adds an MTMarker and an MTTextPopup to the map once it's initialized.
 *
 * Before using, set your API key:
 * `MTConfig.apiKey = "YOUR_API_KEY"`
 */
@Composable
fun MarkersAndPopups() {
    val context = LocalContext.current
    val controller = remember { MTMapViewController(context) }

    // Add annotations when the map is ready.
    LaunchedEffect(controller) {
        controller.delegate =
            object : MTMapViewDelegate {
                override fun onMapViewInitialized() {
                    val target = LngLat(16.626576, 49.212596) // Brno

                    // Add a red marker
                    val marker = MTMarker(target, android.graphics.Color.RED)
                    controller.style?.addMarker(marker)

                    // Add a standalone text popup near the same location
                    val popup = MTTextPopup(target, "Hello Brno", offset = 10.0)
                    controller.style?.addTextPopup(popup)

                    // Center camera on the annotations
                    controller.easeTo(MTCameraOptions(target))
                }

                override fun onEventTriggered(event: com.maptiler.maptilersdk.events.MTEvent, data: MTData?) {
                    // No-op for this simple example
                }
            }
    }

    DisposableEffect(controller) {
        onDispose { controller.delegate = null }
    }

    MTMapView(
        referenceStyle = MTMapReferenceStyle.STREETS,
        options = MTMapOptions(),
        controller = controller,
        modifier = Modifier.fillMaxSize(),
    )
}

/**
 * Optional Activity wrapper to launch the [MarkersAndPopups] composable.
 */
class MarkersAndPopupsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set your MapTiler Cloud API key
        MTConfig.apiKey = "YOUR_API_KEY"

        setContent {
            MarkersAndPopups()
        }
    }
}
