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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Suppress("FunctionName")
@Composable
fun LocationControl(
    onLocate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val width = 60.dp
    val height = 60.dp

    Row(
        modifier =
            modifier
                .background(Color(0xFFF6F7FD), RoundedCornerShape(30.dp))
                .height(height)
                .width(width),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onLocate) {
            Icon(Icons.Default.LocationOn, contentDescription = "Locate Me", modifier = Modifier.padding(start = 10.dp))
        }
    }
}
