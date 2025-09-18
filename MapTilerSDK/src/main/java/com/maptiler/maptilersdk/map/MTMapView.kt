/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.viewinterop.AndroidView
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import com.maptiler.maptilersdk.map.style.MTStyle

/**
 * Object representing the map on the screen.
 */
@Suppress("FunctionName")
@Composable
fun MTMapView(
    referenceStyle: MTMapReferenceStyle,
    options: MTMapOptions,
    controller: MTMapViewController,
    modifier: Modifier = Modifier,
    styleVariant: MTMapStyleVariant? = null,
) {
    val scope = rememberCoroutineScope()
    val webView = remember { controller.getAttachableWebView() }

    LaunchedEffect(Unit) {
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

    key(webView) {
        AndroidView(
            factory = { webView },
            modifier =
                modifier.onGloballyPositioned { coords ->
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
    }
}
