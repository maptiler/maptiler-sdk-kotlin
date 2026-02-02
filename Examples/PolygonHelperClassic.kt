package com.maptiler.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.helpers.MTPolygonLayerHelper
import com.maptiler.maptilersdk.helpers.MTPolygonLayerOptions
import com.maptiler.maptilersdk.helpers.MTStringOrZoomStringValues
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapViewClassic
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle

/**
 * XML/View example showing how to add a polygon layer using MTPolygonLayerHelper with MTMapViewClassic.
 */
class PolygonHelperClassicActivity : ComponentActivity() {
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
                                [ -122.52, 37.70 ], [ -122.35, 37.70 ], [ -122.35, 37.82 ], [ -122.52, 37.82 ], [ -122.52, 37.70 ]
                              ]
                            ]
                          }
                        }
                      ]
                    }
                    """.trimIndent()

                // Clean up any previous run
                style.removeLayerById("classic-polygon")
                style.removeSourceById("classic-polygon-source")

                val helper: MTPolygonLayerHelper = style.polygonHelper()
                val opts =
                    MTPolygonLayerOptions(
                        data = polygonGeoJson,
                        layerId = "classic-polygon",
                        sourceId = "classic-polygon-source",
                        fillColor = MTStringOrZoomStringValues.StringValue("#33AAFF"),
                        fillOpacity = com.maptiler.maptilersdk.helpers.MTNumberOrZoomNumberValues.Number(0.4),
                        outline = true,
                        outlineColor = MTStringOrZoomStringValues.StringValue("#0044AA"),
                        outlineWidth = com.maptiler.maptilersdk.helpers.MTNumberOrZoomNumberValues.Number(2.0),
                    )
                helper.addPolygon(opts)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.destroy()
    }
}

