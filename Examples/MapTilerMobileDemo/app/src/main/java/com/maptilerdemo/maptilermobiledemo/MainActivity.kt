package com.maptilerdemo.maptilermobiledemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.maptiler.maptilersdk.MTConfig

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
}
