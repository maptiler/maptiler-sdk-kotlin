package com.maptiler.examples

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle

/**
 * Basic Compose example showing how to render a map using [MTMapView].
 *
 * Set your API key once before using the SDK:
 * `MTConfig.apiKey = "YOUR_API_KEY"`
 */
@Composable
fun BasicMapView() {
    val context = LocalContext.current
    val controller = remember { MTMapViewController(context) }

    MTMapView(
        referenceStyle = MTMapReferenceStyle.STREETS,
        options = MTMapOptions(),
        controller = controller,
        modifier = Modifier.fillMaxSize(),
    )
}

/**
 * Optional Activity wrapper to run the [BasicMapView] composable directly.
 */
class BasicMapComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set your MapTiler Cloud API key
        MTConfig.apiKey = "YOUR_API_KEY"

        setContent {
            BasicMapView()
        }
    }
}

