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
import androidx.compose.material.icons.filled.Menu
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
import com.maptiler.maptilersdk.map.style.layer.MTLayerType

@Composable
fun LayerControl(
    onSelect: (MTLayerType) -> Unit,
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
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.Menu, contentDescription = "Layer options")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                Text(
                    "Add Layer",
                    color = Color.Black,
                    modifier =
                        Modifier
                            .padding(start = 10.dp),
                )

                DropdownMenuItem(
                    text = { Text("Symbol", color = Color.Black) },
                    onClick = { onSelect(MTLayerType.SYMBOL) },
                )

                DropdownMenuItem(
                    text = { Text("Fill", color = Color.Black) },
                    onClick = { onSelect(MTLayerType.FILL) },
                )

                DropdownMenuItem(
                    text = { Text("Line", color = Color.Black) },
                    onClick = { onSelect(MTLayerType.LINE) },
                )
            }
        }
    }
}
