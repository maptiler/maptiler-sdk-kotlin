package com.maptiler.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapViewClassic
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle

/**
 * XML/View example that demonstrates helper converters/value types:
 * - Zoom ramps (number/string)
 * - Property-based map for heatmap weight
 * - Radius option as zoom-based values
 * - Dash array option (string pattern)
 */
class HelperConvertersClassicActivity : ComponentActivity() {
    private lateinit var mapView: MTMapViewClassic
    private lateinit var controller: MTMapViewController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MTConfig.apiKey = "YOUR_API_KEY"
        controller = MTMapViewController(baseContext)
        setContentView(R.layout.activity_basic_classic_map_view)

        mapView = findViewById(R.id.classicMapView)
        mapView.initialize(
            referenceStyle = MTMapReferenceStyle.DATAVIZ,
            options = MTMapOptions(),
            controller = controller,
            styleVariant = null,
        )

        controller.delegate = object : com.maptiler.maptilersdk.map.MTMapViewDelegate {
            override fun onMapViewInitialized() {
                val style = controller.style ?: return

                // 1) Heatmap with property-based weights and zoom-based radius
                val earthquakes = "https://docs.maptiler.com/sdk-js/assets/earthquakes.geojson"
                style.removeLayerById("conv-heatmap-classic")
                style.removeSourceById("conv-heatmap-src-classic")

                val heatmapHelper: MTHeatmapLayerHelper = style.heatmapHelper()
                heatmapHelper.addHeatmap(
                    MTHeatmapLayerOptions(
                        data = earthquakes,
                        layerId = "conv-heatmap-classic",
                        sourceId = "conv-heatmap-src-classic",
                        property = "mag",
                        weight = MTNumberOrPropertyValues.PropertyMap(
                            listOf(
                                MTHelperPropertyValue(0.0, 0.0),
                                MTHelperPropertyValue(5.0, 1.0),
                            ),
                        ),
                        radius = MTRadiusOption.Zoom(
                            listOf(
                                MTZoomNumberValue(3.0, 5.0),
                                MTZoomNumberValue(8.0, 20.0),
                            ),
                        ),
                        intensity = MTNumberOrZoomNumberValues.Number(1.0),
                    ),
                )

                // 2) Polyline with string color ramp and dash pattern
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

                style.removeLayerById("conv-line-classic")
                style.removeSourceById("conv-line-src-classic")
                val polylineHelper: MTPolylineLayerHelper = style.polylineHelper()
                polylineHelper.addPolyline(
                    MTPolylineLayerOptions(
                        data = lineGeoJson,
                        layerId = "conv-line-classic",
                        sourceId = "conv-line-src-classic",
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
                        lineDashArray = MTDashArrayOption.StringValue("2 1 4 1"),
                    ),
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.destroy()
    }
}

