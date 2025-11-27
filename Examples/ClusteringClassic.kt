package com.maptiler.examples

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapViewClassic
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import com.maptiler.maptilersdk.map.style.MTStyle
import com.maptiler.maptilersdk.map.style.dsl.Expression
import com.maptiler.maptilersdk.map.style.dsl.Filter
import com.maptiler.maptilersdk.map.style.dsl.MTFeatureKey
import com.maptiler.maptilersdk.map.style.dsl.PropertyValue
import com.maptiler.maptilersdk.map.style.layer.circle.MTCircleLayer
import com.maptiler.maptilersdk.map.style.layer.circle.colorConst
import com.maptiler.maptilersdk.map.style.layer.circle.colorExpr
import com.maptiler.maptilersdk.map.style.layer.circle.radiusConst
import com.maptiler.maptilersdk.map.style.layer.circle.radiusExpr
import com.maptiler.maptilersdk.map.style.layer.symbol.MTSymbolLayer
import com.maptiler.maptilersdk.map.style.layer.symbol.textAllowOverlap
import com.maptiler.maptilersdk.map.style.layer.symbol.MTTextAnchor
import com.maptiler.maptilersdk.map.style.layer.symbol.textAnchor
import com.maptiler.maptilersdk.map.style.layer.symbol.textColorConst
import com.maptiler.maptilersdk.map.style.layer.symbol.textField
import com.maptiler.maptilersdk.map.style.layer.symbol.textFont
import com.maptiler.maptilersdk.map.style.layer.symbol.textSize
import com.maptiler.maptilersdk.map.style.dsl.MTTextToken
import com.maptiler.maptilersdk.map.style.source.MTGeoJSONSource
import java.net.URL

/**
 * View (XML) example building the same clustering demo as Compose.
 */
class ClusteringClassicActivity : ComponentActivity() {
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
            styleVariant = MTMapStyleVariant.DARK,
        )

        // Build once map/bridge is ready
        controller.delegate = object : com.maptiler.maptilersdk.map.MTMapViewDelegate {
            override fun onMapViewInitialized() {
                setupClusters(controller.style!!)
            }
        }
    }

    private fun setupClusters(style: MTStyle) {
        val src = MTGeoJSONSource.fromUrl(
            identifier = "earthquakes",
            url = URL("https://docs.maptiler.com/sdk-js/assets/earthquakes.geojson"),
        ).apply {
            isCluster = true
            clusterRadius = 50.0
            clusterMaxZoom = 14.0
        }
        style.addSource(src)

        val clusters =
            MTCircleLayer("clusters", src.identifier)
                .apply {
                    withFilter(Filter.clusters())
                    colorExpr(
                        Expression.step(
                            input = Expression.get(MTFeatureKey.POINT_COUNT),
                            default = PropertyValue.Color(Color.parseColor("#51bbd6")),
                            stops = listOf(
                                100.0 to PropertyValue.Color(Color.parseColor("#f1f075")),
                                750.0 to PropertyValue.Color(Color.parseColor("#f28cb1")),
                            ),
                        ),
                    )
                    radiusExpr(
                        Expression.step(
                            input = Expression.get(MTFeatureKey.POINT_COUNT),
                            default = PropertyValue.Num(20.0),
                            stops = listOf(
                                100.0 to PropertyValue.Num(30.0),
                                750.0 to PropertyValue.Num(40.0),
                            ),
                        ),
                    )
                }
        style.addLayer(clusters)

        val labels =
            MTSymbolLayer("clusterCount", src.identifier)
                .apply {
                    withFilter(Filter.clusters())
                    textField(MTTextToken.POINT_COUNT_ABBREVIATED)
                    textSize(12.0)
                    textAllowOverlap(true)
                    textAnchor(MTTextAnchor.CENTER)
                    textFont(listOf("DIN Offc Pro Medium", "Arial Unicode MS Bold"))
                    textColorConst(Color.WHITE)
                }
        style.addLayer(labels)

        val unclustered =
            MTCircleLayer("unclusteredPoint", src.identifier)
                .apply {
                    withFilter(Filter.unclustered())
                    colorConst(Color.parseColor("#11b4da"))
                    radiusConst(4.0)
                }
        style.addLayer(unclustered)
    }
}
