/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Suppress("FunctionName")
@Composable
fun NavigationControl(
    onFlyTo: () -> Unit,
    onEaseTo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shadowElevation = 4.dp
    val shadowShape = RoundedCornerShape(30.dp)
    val containerColor = Color(0xFFF6F7FD)
    val width = 190.dp
    val height = 50.dp
    val fontSize = 18.sp

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = onFlyTo,
            modifier =
                Modifier
                    .width(width)
                    .height(height)
                    .shadow(elevation = shadowElevation, shape = shadowShape)
                    .clip(shadowShape),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = Color.Black,
                ),
        ) {
            Text("Fly To Unterägeri", fontSize = fontSize)
        }

        Button(
            onClick = onEaseTo,
            modifier =
                Modifier
                    .width(width)
                    .height(height)
                    .shadow(elevation = shadowElevation, shape = shadowShape)
                    .clip(shadowShape),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = Color.Black,
                ),
        ) {
            Text("Ease To Brno", fontSize = fontSize)
        }
    }
}
