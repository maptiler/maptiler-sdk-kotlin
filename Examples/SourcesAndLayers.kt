package com.maptiler.examples

import android.os.Bundle
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.MTMapViewDelegate
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.layer.line.MTLineLayer
import com.maptiler.maptilersdk.map.style.source.MTVectorTileSource
import com.maptiler.maptilersdk.map.types.MTData
import java.net.URL

/**
 * Compose example that adds an MTVectorTileSource (contours) and a MTLineLayer using that source.
 *
 * Replace the API key placeholder or set MTConfig.apiKey beforehand.
 */
@Composable
fun SourcesAndLayers() {
    val controller = remember { MTMapViewController(baseContext) }

    LaunchedEffect(controller) {
        controller.delegate =
            object : MTMapViewDelegate {
                override fun onMapViewInitialized() {
                    // Vector tile source (contours) using tile template URL
                    val contoursUrl =
                        URL("https://api.maptiler.com/tiles/contours-v2/{z}/{x}/{y}.pbf?key=${MTConfig.apiKey}")
                    val source = MTVectorTileSource(identifier = "contours-source", url = contoursUrl)
                    controller.style?.addSource(source)

                    // Line layer referencing the contours source
                    val lineLayer = MTLineLayer(identifier = "contours-line", sourceIdentifier = source.identifier)
                    lineLayer.sourceLayer = "contour"
                    lineLayer.color = Color.RED
                    lineLayer.width = 1.5
                    controller.style?.addLayer(lineLayer)
                }

                override fun onEventTriggered(event: com.maptiler.maptilersdk.events.MTEvent, data: MTData?) {
                    // No-op for this example
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
 * Optional Activity wrapper to launch the [SourcesAndLayers] composable.
 */
class SourcesAndLayersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set your MapTiler Cloud API key
        MTConfig.apiKey = "YOUR_API_KEY"

        setContent {
            SourcesAndLayers()
        }
    }
}

