/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Suppress("FunctionName")
@Composable
fun TerrainGlobeControl(
    onTerrain: () -> Unit,
    onGlobe: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shadowElevation = 4.dp
    val shadowShape = RoundedCornerShape(30.dp)
    val containerColor = Color(0xFFF6F7FD)
    val width = 60.dp
    val height = 60.dp
    val fontSize = 34.sp

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = onTerrain,
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
            Text(
                "#",
                fontSize = fontSize,
                textAlign = TextAlign.Right,
                modifier =
                    Modifier
                        .padding(start = 15.dp),
            )
        }

        Button(
            onClick = onGlobe,
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
            Text(
                "@",
                fontSize = fontSize,
                textAlign = TextAlign.Right,
                modifier =
                    Modifier
                        .padding(start = 20.dp),
            )
        }
    }
}
