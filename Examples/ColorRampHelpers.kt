package com.maptiler.examples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.colorramp.MTColorRamp
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerHelper
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerOptions
import com.maptiler.maptilersdk.helpers.MTPointLayerHelper
import com.maptiler.maptilersdk.helpers.MTPointLayerOptions
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import kotlinx.coroutines.launch

/**
 * Compose example demonstrating color ramp selection and the point/heatmap helpers
 * on the earthquakes dataset.
 */
@Composable
fun ColorRampHelpers() {
    val controller = remember { MTMapViewController(baseContext) }

    Box(Modifier.fillMaxSize()) {
        MTMapView(
            referenceStyle = MTMapReferenceStyle.DATAVIZ,
            options = MTMapOptions(),
            controller = controller,
            modifier = Modifier.fillMaxSize(),
        )

        // Ramp picker + helper actions
        Column(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            val ramps = remember {
                listOf(
                    "TURBO",
                    "VIRIDIS",
                    "MAGMA",
                    "PLASMA",
                    "INFERNO",
                    "PORTLAND",
                )
            }
            var rampIndex by remember { mutableStateOf(0) }
            val selectedName = ramps[rampIndex % ramps.size]
            val selectedRamp: MutableState<MTColorRamp?> = remember { mutableStateOf(null) }
            val scope = rememberCoroutineScope()

            Text("Ramp: $selectedName", color = Color.Black)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { rampIndex = if (rampIndex - 1 < 0) ramps.lastIndex else rampIndex - 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0), contentColor = Color.Black),
                ) { Text("Prev", color = Color.Black) }

                Button(
                    onClick = { rampIndex = (rampIndex + 1) % ramps.size },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0), contentColor = Color.Black),
                ) { Text("Next", color = Color.Black) }

                Button(
                    onClick = {
                        val style = controller.style ?: return@Button
                        scope.launch {
                            val collection = style.colorRampCollection()
                            val ramp = when (selectedName) {
                                "TURBO" -> collection.turbo()
                                "VIRIDIS" -> collection.viridis()
                                "MAGMA" -> collection.magma()
                                "PLASMA" -> collection.plasma()
                                "INFERNO" -> collection.inferno()
                                "PORTLAND" -> collection.portland()
                                else -> collection.turbo()
                            }
                            selectedRamp.value = ramp
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0FF), contentColor = Color.Black),
                ) { Text("Load", color = Color.Black) }
            }

            val earthquakesUrl = "https://docs.maptiler.com/sdk-js/assets/earthquakes.geojson"
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = {
                        val style = controller.style ?: return@Button
                        style.removeLayerById("eq-points")
                        style.removeLayerById("eq-points_cluster")
                        style.removeLayerById("eq-points_label")
                        style.removeSourceById("eq-source-points")

                        val helper: MTPointLayerHelper = style.pointHelper()
                        val opts =
                            MTPointLayerOptions(
                                data = earthquakesUrl,
                                layerId = "eq-points",
                                sourceId = "eq-source-points",
                                property = "mag",
                                cluster = true,
                            )
                        val ramp = selectedRamp.value
                        if (ramp != null) helper.addPoint(opts, ramp) else helper.addPoint(opts)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0FFE0), contentColor = Color.Black),
                ) { Text("Add Points (eq)", color = Color.Black) }

                Button(
                    onClick = {
                        val style = controller.style ?: return@Button
                        style.removeLayerById("eq-heatmap")
                        style.removeSourceById("eq-source-heatmap")

                        val helper: MTHeatmapLayerHelper = style.heatmapHelper()
                        val opts =
                            MTHeatmapLayerOptions(
                                data = earthquakesUrl,
                                layerId = "eq-heatmap",
                                sourceId = "eq-source-heatmap",
                                property = "mag",
                            )
                        helper.addHeatmap(opts, selectedRamp.value)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE0E0), contentColor = Color.Black),
                ) { Text("Add Heatmap (eq)", color = Color.Black) }
            }
        }
    }
}

/** Optional Activity wrapper to run the composable. */
class ColorRampHelpersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MTConfig.apiKey = "YOUR_API_KEY"
        setContent { ColorRampHelpers() }
    }
}

