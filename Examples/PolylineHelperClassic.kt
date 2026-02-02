package com.maptiler.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.helpers.MTDashArrayOption
import com.maptiler.maptilersdk.helpers.MTPolylineLayerHelper
import com.maptiler.maptilersdk.helpers.MTPolylineLayerOptions
import com.maptiler.maptilersdk.helpers.MTStringOrZoomStringValues
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapViewClassic
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle

/**
 * XML/View example showing how to add a polyline layer using MTPolylineLayerHelper with MTMapViewClassic.
 */
class PolylineHelperClassicActivity : ComponentActivity() {
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

                val lineGeoJson =
                    """
                    { "type":"FeatureCollection","features":[
                      { "type":"Feature",
                        "properties": { "name":"Sample Line" },
                        "geometry":{
                          "type":"LineString",
                          "coordinates":[
                            [ -122.48, 37.81 ], [ -122.40, 37.80 ], [ -122.39, 37.77 ], [ -122.41, 37.74 ]
                          ]
                        }
                      }
                    ]}
                    """.trimIndent()

                // Clean up any previous run
                style.removeLayerById("classic-polyline")
                style.removeSourceById("classic-polyline-source")

                val helper: MTPolylineLayerHelper = style.polylineHelper()
                val opts =
                    MTPolylineLayerOptions(
                        data = lineGeoJson,
                        layerId = "classic-polyline",
                        sourceId = "classic-polyline-source",
                        lineColor = MTStringOrZoomStringValues.StringValue("#E63946"),
                        lineWidth = com.maptiler.maptilersdk.helpers.MTNumberOrZoomNumberValues.Number(3.0),
                        lineOpacity = com.maptiler.maptilersdk.helpers.MTNumberOrZoomNumberValues.Number(0.9),
                        lineDashArray = MTDashArrayOption.Numbers(listOf(2.0, 1.0)),
                    )
                helper.addPolyline(opts)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.destroy()
    }
}

