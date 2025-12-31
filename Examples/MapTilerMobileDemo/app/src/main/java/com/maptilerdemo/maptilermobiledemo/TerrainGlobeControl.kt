/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val shadowShape = RoundedCornerShape(8.dp)
    val containerColor = Color(0xFFF6F7FD)
    val fontSize = 14.sp

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = onTerrain,
            modifier =
                Modifier
                    .clip(shadowShape),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = Color.Black,
                ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text("Terrain", fontSize = fontSize, textAlign = TextAlign.Center)
        }

        Button(
            onClick = onGlobe,
            modifier =
                Modifier
                    .clip(shadowShape),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = Color.Black,
                ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text("Projection", fontSize = fontSize, textAlign = TextAlign.Center)
        }
    }
}
