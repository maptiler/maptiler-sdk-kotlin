/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maptiler.maptilersdk.map.LngLat

@Composable
fun JumpControl(
    onJump: (LngLat) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier,
    ) {
        Row(
            modifier =
                Modifier
                    .background(Color(0xFFF6F7FD), RoundedCornerShape(30.dp)),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Jump To:",
                color = Color.Black,
                modifier =
                    Modifier
                        .padding(start = 16.dp),
            )
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text("Tokyo", color = Color.Black) },
                    onClick = { onJump(LngLat(139.839478, 35.652832)) },
                )
                DropdownMenuItem(
                    text = { Text("Miami", color = Color.Black) },
                    onClick = { onJump(LngLat(-80.139198, 25.793449)) },
                )
                DropdownMenuItem(
                    text = { Text("Nairobi", color = Color.Black) },
                    onClick = { onJump(LngLat(36.81722, -1.286389)) },
                )
            }
        }
    }
}
