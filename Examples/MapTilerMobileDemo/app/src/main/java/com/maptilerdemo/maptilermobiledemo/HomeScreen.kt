/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle

@Composable
fun HomeScreen(
    navController: NavController,
    context: Context,
) {
    val mapController = MapController(context)

    MTMapView(
        MTMapReferenceStyle.STREETS,
        MTMapOptions(),
        mapController.controller,
        modifier =
            Modifier
                .fillMaxSize(),
    )
}
