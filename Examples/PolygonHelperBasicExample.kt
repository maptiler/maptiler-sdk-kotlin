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
import com.maptiler.maptilersdk.helpers.MTPolygonLayerHelper
import com.maptiler.maptilersdk.helpers.MTPolygonLayerOptions
import com.maptiler.maptilersdk.helpers.MTStringOrZoomStringValues
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import kotlinx.coroutines.launch

/**
 * Minimal Compose example: add a polygon layer from inline GeoJSON
 * using MTPolygonLayerHelper with basic styling.
 */
@Composable
fun PolygonHelperBasicExample() {
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
                    // Simple rectangle around part of San Francisco
                    val polygonGeoJson =
                        """
                        {
                          "type": "FeatureCollection",
                          "features": [
                            {
                              "type": "Feature",
                              "properties": { "name": "Sample Polygon" },
                              "geometry": {
                                "type": "Polygon",
                                "coordinates": [
                                  [
                                    [ -122.52, 37.70 ],
                                    [ -122.35, 37.70 ],
                                    [ -122.35, 37.82 ],
                                    [ -122.52, 37.82 ],
                                    [ -122.52, 37.70 ]
                                  ]
                                ]
                              }
                            }
                          ]
                        }
                        """.trimIndent()

                    // Clean up any previous run
                    style.removeLayerById("basic-polygon")
                    style.removeSourceById("basic-polygon-source")

                    val helper: MTPolygonLayerHelper = style.polygonHelper()
                    val opts =
                        MTPolygonLayerOptions(
                            data = polygonGeoJson,
                            layerId = "basic-polygon",
                            sourceId = "basic-polygon-source",
                            // Basic fill + outline
                            fillColor = MTStringOrZoomStringValues.StringValue("#33AAFF"),
                            fillOpacity = com.maptiler.maptilersdk.helpers.MTNumberOrZoomNumberValues.Number(0.4),
                            outline = true,
                            outlineColor = MTStringOrZoomStringValues.StringValue("#0044AA"),
                            outlineWidth = com.maptiler.maptilersdk.helpers.MTNumberOrZoomNumberValues.Number(2.0),
                        )
                    helper.addPolygon(opts)
                }
            },
        ) { Text("Add Polygon") }
    }
}

class PolygonHelperBasicExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure your MapTiler API key
        MTConfig.apiKey = "YOUR_API_KEY"
        setContent { PolygonHelperBasicExample() }
    }
}

