package com.maptiler.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerHelper
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerOptions
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import kotlinx.coroutines.launch

/**
 * Simple Compose example: add a heatmap layer from a GeoJSON URL
 * using MTHeatmapLayerHelper with default ramp and basic options.
 */
@Composable
fun HeatmapHelperBasicExample() {
    val context = LocalContext.current
    val controller = remember { MTMapViewController(context) }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        MTMapView(
            referenceStyle = MTMapReferenceStyle.DATAVIZ,
            options = MTMapOptions(),
            controller = controller,
            modifier = Modifier.fillMaxSize(),
        )

        Button(
            modifier = Modifier.align(Alignment.TopStart),
            onClick = {
                val style = controller.style ?: return@Button
                scope.launch {
                    val dataUrl = "https://docs.maptiler.com/sdk-js/assets/earthquakes.geojson"
                    val helper: MTHeatmapLayerHelper = style.heatmapHelper()
                    val opts = MTHeatmapLayerOptions(
                        data = dataUrl,
                        layerId = "basic-heatmap",
                        sourceId = "basic-heatmap-source",
                        // Weight by magnitude property
                        property = "mag",
                        // Disambiguate constructor overload without changing defaults
                        intensity = null as com.maptiler.maptilersdk.helpers.MTNumberOrZoomNumberValues?,
                    )
                    helper.addHeatmap(opts)
                }
            },
        ) { Text("Add Heatmap") }
    }
}

class HeatmapHelperBasicExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure your MapTiler API key
        MTConfig.apiKey = "YOUR_API_KEY"
        setContent { HeatmapHelperBasicExample() }
    }
}
