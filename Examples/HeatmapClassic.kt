package com.maptiler.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerHelper
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerOptions
import com.maptiler.maptilersdk.helpers.MTNumberOrZoomNumberValues
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapViewClassic
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle

/**
 * XML/View example showing how to add a heatmap using MTHeatmapLayerHelper with MTMapViewClassic.
 *
 * Layout XML (reuse the basic layout used by other Classic examples):
 * - res/layout/activity_basic_classic_map_view.xml
 */
class HeatmapClassicActivity : ComponentActivity() {
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

        // Once the map is ready, add the heatmap layer
        controller.delegate = object : com.maptiler.maptilersdk.map.MTMapViewDelegate {
            override fun onMapViewInitialized() {
                val style = controller.style ?: return

                val earthquakesUrl = "https://docs.maptiler.com/sdk-js/assets/earthquakes.geojson"
                val helper: MTHeatmapLayerHelper = style.heatmapHelper()

                // Option A: default ramp (transparent start) with magnitude weighting
                val opts =
                    MTHeatmapLayerOptions(
                        data = earthquakesUrl,
                        layerId = "classic-heatmap",
                        sourceId = "classic-heatmap-source",
                        // Weight by magnitude property
                        property = "mag",
                        // Disambiguate constructor overload without changing defaults
                        intensity = null as MTNumberOrZoomNumberValues?,
                    )
                helper.addHeatmap(opts)

                // Option B: use a built-in color ramp instead
                // val ramp: MTColorRamp = style.colorRampCollection().turbo()
                // helper.addHeatmap(opts, ramp)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.destroy()
    }
}
