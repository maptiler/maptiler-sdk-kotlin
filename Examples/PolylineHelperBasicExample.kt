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
import com.maptiler.maptilersdk.helpers.MTDashArrayOption
import com.maptiler.maptilersdk.helpers.MTPolylineLayerHelper
import com.maptiler.maptilersdk.helpers.MTPolylineLayerOptions
import com.maptiler.maptilersdk.helpers.MTStringOrZoomStringValues
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import kotlinx.coroutines.launch

/**
 * Minimal Compose example: add a polyline layer from inline GeoJSON
 * using MTPolylineLayerHelper with basic styling and a dash pattern.
 */
@Composable
fun PolylineHelperBasicExample() {
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
                    val lineGeoJson =
                        """
                        { "type":"FeatureCollection","features":[
                          { "type":"Feature",
                            "properties": { "name":"Sample Line" },
                            "geometry":{
                              "type":"LineString",
                              "coordinates":[
                                [ -122.48, 37.81 ],
                                [ -122.40, 37.80 ],
                                [ -122.39, 37.77 ],
                                [ -122.41, 37.74 ]
                              ]
                            }
                          }
                        ]}
                        """.trimIndent()

                    // Clean up any previous run
                    style.removeLayerById("basic-polyline")
                    style.removeSourceById("basic-polyline-source")

                    val helper: MTPolylineLayerHelper = style.polylineHelper()
                    val opts =
                        MTPolylineLayerOptions(
                            data = lineGeoJson,
                            layerId = "basic-polyline",
                            sourceId = "basic-polyline-source",
                            lineColor = MTStringOrZoomStringValues.StringValue("#E63946"),
                            lineWidth = com.maptiler.maptilersdk.helpers.MTNumberOrZoomNumberValues.Number(3.0),
                            lineOpacity = com.maptiler.maptilersdk.helpers.MTNumberOrZoomNumberValues.Number(0.9),
                            // Use either numeric or string dash patterns; here numeric values
                            lineDashArray = MTDashArrayOption.Numbers(listOf(2.0, 1.0)),
                        )
                    helper.addPolyline(opts)
                }
            },
        ) { Text("Add Polyline") }
    }
}

class PolylineHelperBasicExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure your MapTiler API key
        MTConfig.apiKey = "YOUR_API_KEY"
        setContent { PolylineHelperBasicExample() }
    }
}

