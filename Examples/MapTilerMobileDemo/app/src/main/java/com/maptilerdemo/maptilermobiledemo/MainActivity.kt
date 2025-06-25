package com.maptilerdemo.maptilermobiledemo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.map.LatLng
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.MTMapViewDelegate
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import com.maptiler.maptilersdk.map.types.MTLanguage
import com.maptiler.maptilersdk.map.types.MTSpecialLanguage

class MainActivity : ComponentActivity() {
    private var mapController: MapController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MTConfig.setAPIKey(BuildConfig.MAPTILER_API_KEY)

        mapController = MapController(baseContext)

        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                MTMapView(
                    MTMapReferenceStyle.STREETS,
                    MTMapOptions(),
                    mapController!!.controller,
                    modifier =
                        Modifier
                            .fillMaxSize(),
                )
            }
        }
    }
}

class MapController(
    private val context: Context,
) : MTMapViewDelegate {
    val controller: MTMapViewController =
        MTMapViewController(context).apply {
            delegate = this@MapController
        }

    override fun onMapViewInitialized() {
        Log.i("Demo App", "Map View Initialized.")
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MaterialTheme {
        Greeting("Android")
    }
}
