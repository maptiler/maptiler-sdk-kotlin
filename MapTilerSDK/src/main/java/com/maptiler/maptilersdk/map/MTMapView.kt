/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.maptiler.maptilersdk.MTConfig
import com.maptiler.maptilersdk.events.MTEvent
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import com.maptiler.maptilersdk.map.style.MTStyle
import com.maptiler.maptilersdk.map.types.MTData

/**
 * Object representing the map on the screen.
 *
 * Map content such as markers, popups, sources and layers can be declared in [content]:
 * ```
 * MTMapView(referenceStyle = MTMapReferenceStyle.STREETS, controller = controller) {
 *     MTMapMarker(position = LngLat(14.43, 50.07), color = Color.Red)
 *     MTMapTextPopup(position = LngLat(14.43, 50.07), text = "Hello Prague")
 * }
 * ```
 *
 * @param referenceStyle Reference style of the map.
 * @param options Initial options of the map. Defaults to [MTMapOptions] with default values.
 * @param controller Controller of the map.
 * @param modifier Modifier applied to the map container.
 * @param styleVariant Optional variant of the reference style.
 * @param content Optional declarative map content, added once the map is ready and removed when it
 * leaves the composition. Sources and layers declared here are not re-added after
 * [MTStyle.setStyle], see [MTMapScope].
 */
@Suppress("FunctionName")
@Composable
fun MTMapView(
    referenceStyle: MTMapReferenceStyle,
    options: MTMapOptions = MTMapOptions(),
    controller: MTMapViewController,
    modifier: Modifier = Modifier,
    styleVariant: MTMapStyleVariant? = null,
    content: (@Composable MTMapScope.() -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val webView = remember { controller.getAttachableWebView() }
    var isAPIKeyInvalid by remember { mutableStateOf(false) }
    val isMapReady = remember { mutableStateOf(false) }
    val mapScope = remember(controller) { MTMapScope(controller, isMapReady) }

    LaunchedEffect(Unit) {
        controller.onInvalidAPIKey = { isAPIKeyInvalid = true }
        controller.bind(scope)
        controller.options = options

        val style = MTStyle(referenceStyle, styleVariant)
        controller.style = style
    }

    DisposableEffect(Unit) {
        onDispose {
            controller.destroy()
        }
    }

    if (content != null) {
        DisposableEffect(controller) {
            val readyDelegate =
                object : MTMapViewContentDelegate {
                    override fun onEvent(
                        event: MTEvent,
                        data: MTData?,
                    ) {
                        if (event == MTEvent.ON_READY) {
                            isMapReady.value = true
                        }
                    }
                }
            controller.addContentDelegate(readyDelegate)

            onDispose {
                controller.removeContentDelegate(readyDelegate)
            }
        }
    }

    key(webView) {
        Box(modifier = modifier) {
            AndroidView(
                factory = { webView },
                modifier =
                    Modifier.fillMaxSize().onGloballyPositioned { coords ->
                        val pos = coords.positionInParent()
                        val size = coords.size
                        controller.updateMapContainerLayout(
                            originX = pos.x.toInt(),
                            originY = pos.y.toInt(),
                            width = size.width,
                            height = size.height,
                        )
                    },
            )

            content?.invoke(mapScope)

            val apiKey = MTConfig.apiKey
            val isAPIKeyMissing = apiKey.isEmpty() || apiKey == "YOUR_API_KEY"
            if (isAPIKeyMissing || isAPIKeyInvalid) {
                Text(
                    text =
                        if (isAPIKeyMissing) {
                            "Map Init Failed - API key not set! Set MTConfig.apiKey first."
                        } else {
                            MTMapViewController.INVALID_API_KEY_MESSAGE
                        },
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .background(Color.White.copy(alpha = 0.8f))
                            .padding(16.dp),
                )
            }
        }
    }
}
