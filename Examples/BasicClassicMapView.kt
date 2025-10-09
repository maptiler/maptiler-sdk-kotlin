package com.maptiler.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapViewClassic
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle

/**
 * Basic XML/View example showing how to render a map using [MTMapViewClassic].
 *
 * Layout XML example (place under `res/layout/activity_basic_classic_map_view.xml`):
 *
 * <?xml version="1.0" encoding="utf-8"?>
 * <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     android:orientation="vertical">
 *
 *     <com.maptiler.maptilersdk.map.MTMapViewClassic
 *         android:id="@+id/classicMapView"
 *         android:layout_width="match_parent"
 *         android:layout_height="match_parent" />
 * </LinearLayout>
 */
class BasicClassicMapViewActivity : ComponentActivity() {
    private lateinit var mapView: MTMapViewClassic
    private lateinit var controller: MTMapViewController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set your MapTiler Cloud API key
        MTConfig.apiKey = "YOUR_API_KEY"

        controller = MTMapViewController(baseContext)

        // Uses the XML described in the KDoc above
        setContentView(R.layout.activity_basic_classic_map_view)

        mapView = findViewById(R.id.classicMapView)
        mapView.initialize(
            referenceStyle = MTMapReferenceStyle.STREETS,
            options = MTMapOptions(),
            controller = controller,
            styleVariant = null,
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.destroy()
    }
}

