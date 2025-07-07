package com.maptilerdemo.maptilermobiledemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.maptiler.maptilersdk.MTConfig

// Classic Map View Imports
// import com.maptiler.maptilersdk.map.MTMapOptions
// import com.maptiler.maptilersdk.map.MTMapViewClassic
// import com.maptiler.maptilersdk.map.MTMapViewController
// import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MTConfig.setAPIKey(BuildConfig.MAPTILER_API_KEY)

        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                MainScreen(baseContext)
            }
        }
    }

    // Classic Map View Example using XML Layout
    // Implementing main_activity_layout.xml

//    private lateinit var mapView: MTMapViewClassic
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        MTConfig.setAPIKey(BuildConfig.MAPTILER_API_KEY)
//        val mapController = MTMapViewController(baseContext)
//        enableEdgeToEdge()
//        setContentView(R.layout.main_activity_layout)
//
//        mapView = findViewById(R.id.classicMapView)
//        mapView.initialize(MTMapReferenceStyle.SATELLITE, MTMapOptions(), mapController)
//    }
}
