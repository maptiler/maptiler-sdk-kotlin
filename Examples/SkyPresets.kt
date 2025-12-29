package com.maptiler.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.events.MTEvent
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.MTMapViewDelegate
import com.maptiler.maptilersdk.map.options.MTCameraOptions
import com.maptiler.maptilersdk.map.options.MTSky
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.types.MTData

/**
 * Compose example demonstrating MTSky + style.setSky() with four presets.
 *
 * Set your API key before running:
 * `MTConfig.apiKey = "YOUR_API_KEY"`
 */
@Composable
fun SkyPresets() {
    val controller = remember { MTMapViewController(baseContext) }
    val selected = remember { mutableStateOf(SkyPreset.CLEAR) }

    LaunchedEffect(controller) {
        controller.delegate =
            object : MTMapViewDelegate {
                override fun onMapViewInitialized() {
                    // Horizon-friendly camera and terrain
                    val pacific = LngLat(-140.0, -20.0)
                    controller.setVerticalFieldOfView(45.0)
                    controller.style?.enableTerrain(1.0)
                    controller.setIsCenterClampedToGround(false)
                    controller.easeTo(MTCameraOptions(center = pacific, zoom = 5.5, bearing = 0.0, pitch = 80.0))

                    // Default preset
                    applySkyPreset(controller, selected.value)
                }

                override fun onEventTriggered(event: MTEvent, data: MTData?) {
                    // no-op
                }
            }
    }

    DisposableEffect(controller) { onDispose { controller.delegate = null } }

    Box(Modifier.fillMaxSize()) {
        MTMapView(
            referenceStyle = MTMapReferenceStyle.STREETS,
            options = MTMapOptions(maxPitch = 85.0),
            controller = controller,
            modifier = Modifier.fillMaxSize(),
        )

        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf(
                SkyPreset.CLEAR to "Clear",
                SkyPreset.SUNSET to "Sunset",
                SkyPreset.NIGHT to "Night",
                SkyPreset.FOGGY to "Foggy",
            ).forEach { (preset, label) ->
                val isSelected = preset == selected.value
                Button(
                    onClick = {
                        selected.value = preset
                        applySkyPreset(controller, preset)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF1976D2) else Color(0xFFE0E0E0),
                        contentColor = Color.Black,
                    ),
                ) { Text(label, color = Color.Black) }
            }
        }
    }
}

private enum class SkyPreset { CLEAR, SUNSET, NIGHT, FOGGY }

private fun applySkyPreset(controller: MTMapViewController, preset: SkyPreset) {
    val sky: MTSky =
        when (preset) {
            SkyPreset.CLEAR ->
                MTSky(
                    skyColor = MTSky.color(Color(red = 0.60f, green = 0.78f, blue = 0.94f).toArgb()),
                    skyHorizonBlend = MTSky.number(0.35),
                    horizonColor = MTSky.color(Color(red = 0.72f, green = 0.85f, blue = 0.96f).toArgb()),
                    horizonFogBlend = MTSky.number(0.0),
                    fogColor = MTSky.color(Color(0xFFEBEBEB).toArgb()),
                    fogGroundBlend = MTSky.number(0.0),
                    atmosphereBlend = MTSky.number(0.20),
                )
            SkyPreset.SUNSET ->
                MTSky(
                    skyColor = MTSky.color(Color(red = 0.34f, green = 0.19f, blue = 0.45f).toArgb()),
                    skyHorizonBlend = MTSky.number(0.55),
                    horizonColor = MTSky.color(Color(red = 1.00f, green = 0.55f, blue = 0.30f).toArgb()),
                    horizonFogBlend = MTSky.number(0.0),
                    fogColor = MTSky.color(Color(0xFFEBEBEB).toArgb()),
                    fogGroundBlend = MTSky.number(0.0),
                    atmosphereBlend = MTSky.number(0.45),
                )
            SkyPreset.NIGHT ->
                MTSky(
                    skyColor = MTSky.color(Color(red = 0.02f, green = 0.06f, blue = 0.16f).toArgb()),
                    skyHorizonBlend = MTSky.number(0.25),
                    horizonColor = MTSky.color(Color(red = 0.04f, green = 0.10f, blue = 0.22f).toArgb()),
                    horizonFogBlend = MTSky.number(0.0),
                    fogColor = MTSky.color(Color(0xFFE6E6E6).toArgb()),
                    fogGroundBlend = MTSky.number(0.0),
                    atmosphereBlend = MTSky.number(0.15),
                )
            SkyPreset.FOGGY ->
                MTSky(
                    skyColor = MTSky.color(Color(red = 0.80f, green = 0.83f, blue = 0.85f).toArgb()),
                    skyHorizonBlend = MTSky.number(0.80),
                    horizonColor = MTSky.color(Color(red = 0.88f, green = 0.90f, blue = 0.91f).toArgb()),
                    horizonFogBlend = MTSky.number(0.60),
                    fogColor = MTSky.color(Color(0xFFEBEBEB).toArgb()),
                    fogGroundBlend = MTSky.number(0.50),
                    atmosphereBlend = MTSky.number(0.35),
                )
        }

    controller.style?.setSky(sky)
}

/** Optional Activity wrapper to run the composable. */
class SkyPresetsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set your MapTiler Cloud API key
        MTConfig.apiKey = "YOUR_API_KEY"

        setContent { SkyPresets() }
    }
}

