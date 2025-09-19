/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maptiler.maptilersdk.annotations.MTCustomAnnotationView
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapViewController

@Suppress("FunctionName")
@Composable
fun CustomViewControl(
    modifier: Modifier = Modifier,
    controller: MTMapViewController,
    position: LngLat,
) {
    MTCustomAnnotationView(controller, position) {
        val shadowElevation = 4.dp
        val shadowShape = RoundedCornerShape(30.dp)
        val containerColor = Color(0xFFF6F7FD)
        val width = 160.dp
        val height = 50.dp

        Row(
            modifier =
                modifier
                    .shadow(elevation = shadowElevation, shape = shadowShape)
                    .clip(shadowShape)
                    .background(containerColor, shadowShape)
                    .width(width)
                    .height(height)
                    .padding(horizontal = 25.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Native Control", color = Color.Black)
        }
    }
}
