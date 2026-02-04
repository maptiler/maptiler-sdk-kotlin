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
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.MTMapViewDelegate
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.layer.hillshade.MTHillshadeLayer
import com.maptiler.maptilersdk.map.style.source.MTRasterDEMSource
import com.maptiler.maptilersdk.map.types.MTData
import java.net.URL

/**
 * Compose example that adds a Raster DEM source (Terrain RGB v2)
 * and a Hillshade layer on top of the base style.
 *
 * The DEM source uses MapTiler Terrain RGB v2 TileJSON endpoint. Ensure MTConfig.apiKey is set.
 */
@Composable
fun HillshadeExample() {
    val context = LocalContext.current
    val controller = remember { MTMapViewController(context) }

    LaunchedEffect(controller) {
        controller.delegate =
            object : MTMapViewDelegate {
                override fun onMapViewInitialized() {
                    val apiKey = MTConfig.apiKey
                    // Raster DEM source (Terrain RGB v2 TileJSON)
                    val demUrl = URL("https://api.maptiler.com/tiles/terrain-rgb-v2/tiles.json?key=$apiKey")
                    val demSourceId = "terrain-rgb-v2"

                    // Add or reuse the raster-dem source
                    try {
                        val demSource = MTRasterDEMSource(demSourceId, demUrl)
                        controller.style?.addSource(demSource)
                    } catch (_: Throwable) {
                        // Source may already exist; ignore for this example
                    }

                    // Add the hillshade layer referencing the DEM source
                    try {
                        val hillshade = MTHillshadeLayer(identifier = "hillshade-layer", sourceIdentifier = demSourceId)
                        controller.style?.addLayer(hillshade)
                    } catch (_: Throwable) {
                        // Layer may already exist; ignore for this example
                    }
                }

                override fun onEventTriggered(event: com.maptiler.maptilersdk.events.MTEvent, data: MTData?) {
                    // No-op
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
 * Optional Activity wrapper to launch the [HillshadeExample] composable.
 */
class HillshadeExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set your MapTiler Cloud API key
        MTConfig.apiKey = "YOUR_API_KEY"

        setContent {
            HillshadeExample()
        }
    }
}
