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
import com.maptiler.maptilersdk.colorramp.MTColorRamp
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerHelper
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerOptions
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import kotlinx.coroutines.launch

/**
 * Minimal example showing how to load a built-in ColorRamp and add a heatmap layer using MTHeatmapLayerHelper.
 */
@Composable
fun HeatmapColorRampExample() {
    val controller = remember { MTMapViewController(baseContext) }
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
                    // Load a built-in ramp (TURBO) and add a heatmap layer with it
                    val collection = style.colorRampCollection()
                    val ramp: MTColorRamp = collection.turbo()

                    val dataUrl = "https://docs.maptiler.com/sdk-js/assets/earthquakes.geojson"
                    val helper: MTHeatmapLayerHelper = style.heatmapHelper()
                    val opts = MTHeatmapLayerOptions(
                        data = dataUrl,
                        layerId = "example-heatmap",
                        sourceId = "example-heatmap-source",
                        property = "mag",
                    )
                    helper.addHeatmap(opts, ramp)
                }
            },
        ) { Text("Add Heatmap with ColorRamp") }
    }
}

class HeatmapColorRampExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HeatmapColorRampExample() }
    }
}

