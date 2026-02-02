package com.maptiler.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.helpers.MTDashArrayOption
import com.maptiler.maptilersdk.helpers.MTHelperPropertyValue
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerHelper
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerOptions
import com.maptiler.maptilersdk.helpers.MTNumberOrPropertyValues
import com.maptiler.maptilersdk.helpers.MTNumberOrZoomNumberValues
import com.maptiler.maptilersdk.helpers.MTPolylineLayerHelper
import com.maptiler.maptilersdk.helpers.MTPolylineLayerOptions
import com.maptiler.maptilersdk.helpers.MTRadiusOption
import com.maptiler.maptilersdk.helpers.MTStringOrZoomStringValues
import com.maptiler.maptilersdk.helpers.MTZoomNumberValue
import com.maptiler.maptilersdk.helpers.MTZoomStringValue
import com.maptiler.maptilersdk.helpers.toHexString
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import kotlinx.coroutines.launch

/**
 * Examples showcasing helper converters and value types:
 * - Zoom-based ramps for numbers and strings
 * - Property-based values (heatmap weight)
 * - Radius option union (number | zoom ramp | property map)
 * - Dash array (numbers vs string pattern)
 * - Color helpers from Android color ints
 */
@Composable
fun HelperConvertersExamples() {
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

        Column(
            modifier = Modifier.align(Alignment.TopStart).padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // 1) Points with zoom-based radius and color ramp
            Button(onClick = {
                val style = controller.style ?: return@Button
                scope.launch {
                    val earthquakes = "https://docs.maptiler.com/sdk-js/assets/earthquakes.geojson"
                    style.removeLayerById("conv-points")
                    style.removeLayerById("conv-points_cluster")
                    style.removeLayerById("conv-points_label")
                    style.removeSourceById("conv-points-src")

                    val pointHelper = style.pointHelper()
                    pointHelper.addPoint(
                        com.maptiler.maptilersdk.helpers.MTPointLayerOptions(
                            data = earthquakes,
                            layerId = "conv-points",
                            sourceId = "conv-points-src",
                            property = "mag",
                            // Zoom-based radius: smaller at low zoom, larger at high zoom
                            pointRadius = MTNumberOrZoomNumberValues.Ramp(
                                listOf(
                                    MTZoomNumberValue(3.0, 1.0),
                                    MTZoomNumberValue(6.0, 3.0),
                                    MTZoomNumberValue(10.0, 8.0),
                                ),
                            ),
                            // Use Android color int converted to hex string
                            outlineColor = MTStringOrZoomStringValues.StringValue(
                                Color.Black.toArgb().toHexString(withAlpha = true),
                            ),
                            outlineWidth = MTNumberOrZoomNumberValues.Number(1.0),
                            cluster = true,
                        ),
                    )
                }
            }) { Text("Points: Zoom radius + color int") }

            // 2) Heatmap with radius as zoom ramp and property-based weights
            Button(onClick = {
                val style = controller.style ?: return@Button
                scope.launch {
                    val earthquakes = "https://docs.maptiler.com/sdk-js/assets/earthquakes.geojson"
                    style.removeLayerById("conv-heatmap")
                    style.removeSourceById("conv-heatmap-src")

                    val heatmapHelper: MTHeatmapLayerHelper = style.heatmapHelper()
                    heatmapHelper.addHeatmap(
                        MTHeatmapLayerOptions(
                            data = earthquakes,
                            layerId = "conv-heatmap",
                            sourceId = "conv-heatmap-src",
                            property = "mag",
                            // Property-based weights mapping a property value to a weight
                            weight = MTNumberOrPropertyValues.PropertyMap(
                                listOf(
                                    MTHelperPropertyValue(propertyValue = 0.0, value = 0.0),
                                    MTHelperPropertyValue(propertyValue = 5.0, value = 1.0),
                                ),
                            ),
                            // Radius as a zoom-based ramp
                            radius = MTRadiusOption.Zoom(
                                listOf(
                                    MTZoomNumberValue(3.0, 5.0),
                                    MTZoomNumberValue(8.0, 20.0),
                                ),
                            ),
                            intensity = MTNumberOrZoomNumberValues.Number(1.0),
                        ),
                    )
                }
            }) { Text("Heatmap: Property weights + zoom radius") }

            // 3) Polyline demonstrating string ramp + numeric dash array
            Button(onClick = {
                val style = controller.style ?: return@Button
                scope.launch {
                    // Short line near SF
                    val lineGeoJson =
                        """
                        { "type":"FeatureCollection","features":[
                          { "type":"Feature",
                            "properties": { "name":"Ramp Line" },
                            "geometry":{
                              "type":"LineString",
                              "coordinates":[
                                [ -122.46, 37.81 ], [ -122.42, 37.79 ], [ -122.40, 37.77 ]
                              ]
                            }
                          }
                        ]}
                        """.trimIndent()

                    style.removeLayerById("conv-line")
                    style.removeSourceById("conv-line-src")

                    val helper: MTPolylineLayerHelper = style.polylineHelper()
                    helper.addPolyline(
                        MTPolylineLayerOptions(
                            data = lineGeoJson,
                            layerId = "conv-line",
                            sourceId = "conv-line-src",
                            // Color as a zoom-based string ramp (could also use ColorRamp elsewhere)
                            lineColor = MTStringOrZoomStringValues.Ramp(
                                listOf(
                                    MTZoomStringValue(3.0, "#457B9D"),
                                    MTZoomStringValue(8.0, "#1D3557"),
                                ),
                            ),
                            lineWidth = MTNumberOrZoomNumberValues.Ramp(
                                listOf(
                                    MTZoomNumberValue(3.0, 2.0),
                                    MTZoomNumberValue(10.0, 6.0),
                                ),
                            ),
                            // Demonstrate dash array via string pattern
                            lineDashArray = MTDashArrayOption.StringValue("2 1 4 1"),
                        ),
                    )
                }
            }) { Text("Polyline: String ramp + dash pattern") }
        }
    }
}

class HelperConvertersExamplesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure your MapTiler API key
        MTConfig.apiKey = "YOUR_API_KEY"
        setContent { HelperConvertersExamples() }
    }
}

