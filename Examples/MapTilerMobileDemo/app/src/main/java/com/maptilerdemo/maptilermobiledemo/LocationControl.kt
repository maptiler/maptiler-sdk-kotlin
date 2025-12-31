/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Suppress("FunctionName")
@Composable
fun LocationControl(
    onLocate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val width = 50.dp
    val height = 50.dp
    val shadowElevation = 4.dp
    val shadowShape = RoundedCornerShape(30.dp)

    Button(
        onClick = onLocate,
        modifier =
            modifier
                .shadow(elevation = shadowElevation, shape = shadowShape)
                .clip(shadowShape)
                .width(width)
                .height(height),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF6F7FD),
                contentColor = Color.Black,
            ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.LocationOn, contentDescription = "Locate Me")
        }
    }
}
