package com.maptiler.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.helpers.MTPointLayerHelper
import com.maptiler.maptilersdk.helpers.MTPointLayerOptions
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapViewClassic
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle

/**
 * XML/View example showing how to add a point layer using MTPointLayerHelper with MTMapViewClassic.
 *
 * Layout XML (reuse the basic layout used by other Classic examples):
 * - res/layout/activity_basic_classic_map_view.xml
 */
class PointHelperClassicActivity : ComponentActivity() {
    private lateinit var mapView: MTMapViewClassic
    private lateinit var controller: MTMapViewController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set your MapTiler Cloud API key
        MTConfig.apiKey = "YOUR_API_KEY"

        controller = MTMapViewController(baseContext)

        // Reuse the standard Classic map layout with a single MTMapViewClassic view
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

                val dataUrl = "https://docs.maptiler.com/sdk-js/assets/earthquakes.geojson"
                val helper: MTPointLayerHelper = style.pointHelper()
                val opts =
                    MTPointLayerOptions(
                        data = dataUrl,
                        layerId = "classic-points",
                        sourceId = "classic-points-source",
                        cluster = true,
                        showLabel = true,
                    )
                helper.addPoint(opts)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.destroy()
    }
}

