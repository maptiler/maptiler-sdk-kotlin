/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Suppress("FunctionName")
@Composable
fun MTMapView(
    options: MTMapOptions,
    controller: MTMapViewController,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        controller.bind(scope)
        controller.options = options
    }

    DisposableEffect(Unit) {
        onDispose {
            controller.destroy()
        }
    }

    AndroidView(
        factory = { controller.getAttachableWebView() },
        modifier = modifier,
    )
}
