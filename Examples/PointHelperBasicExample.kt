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
import com.maptiler.maptilersdk.helpers.MTPointLayerHelper
import com.maptiler.maptilersdk.helpers.MTPointLayerOptions
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import kotlinx.coroutines.launch

/**
 * Minimal Compose example: add a point layer from a GeoJSON URL
 * using MTPointLayerHelper with basic options (defaults for radius/opacity).
 */
@Composable
fun PointHelperBasicExample() {
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
                    val helper: MTPointLayerHelper = style.pointHelper()
                    val opts = MTPointLayerOptions(
                        data = dataUrl,
                        layerId = "basic-points",
                        sourceId = "basic-points-source",
                        // show simple clustering with labels using defaults
                        cluster = true,
                        showLabel = true,
                    )
                    helper.addPoint(opts)
                }
            },
        ) { Text("Add Points") }
    }
}

class PointHelperBasicExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure your MapTiler API key
        MTConfig.apiKey = "YOUR_API_KEY"
        setContent { PointHelperBasicExample() }
    }
}

