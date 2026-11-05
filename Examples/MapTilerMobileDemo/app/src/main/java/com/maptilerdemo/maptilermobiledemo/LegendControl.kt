/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTLayerCategory
import com.maptiler.maptilersdk.map.style.MTLayerQuery
import com.maptiler.maptilersdk.map.style.MTLegendItem
import com.maptiler.maptilersdk.map.style.dsl.PropertyValue
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import kotlinx.coroutines.launch

/**
 * Round button opening the legend sheet.
 */
@Composable
fun LegendControl(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(30.dp)

    Button(
        onClick = onClick,
        modifier =
            modifier
                .shadow(elevation = 4.dp, shape = shape)
                .clip(shape)
                .size(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF6F7FD), contentColor = Color.Black),
        contentPadding = PaddingValues(0.dp),
    ) {
        Icon(Icons.Default.List, contentDescription = "Legend")
    }
}

private val labelsQuery = MTLayerQuery(types = setOf(MTLayerType.SYMBOL))
private val legendQuery = MTLayerQuery(categories = MTLayerCategory.entries.toSet())

/**
 * Demonstrates `MTStyle.getLegend()`, category visibility toggles and query based property updates.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegendSheet(
    controller: MTMapViewController,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf<List<MTLegendItem>>(emptyList()) }
    var labelsVisible by remember { mutableStateOf(true) }
    var buildingsDimmed by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    suspend fun load() {
        val style = controller.style ?: return
        isLoading = true
        items = style.getLegend()
        labelsVisible = style.getStyleLayers(labelsQuery).any { it.isVisible }
        isLoading = false
    }

    // Runs a style change, then reloads the legend.
    fun run(action: suspend (com.maptiler.maptilersdk.map.style.MTStyle) -> Unit) {
        val style = controller.style ?: return
        scope.launch {
            action(style)
            load()
        }
    }

    LaunchedEffect(Unit) { load() }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Layers", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { run { it.setVisibility(legendQuery, true) } }) { Text("Show all") }
                TextButton(onClick = { run { it.setVisibility(legendQuery, false) } }) { Text("Hide all") }
            }

            if (isLoading && items.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            LazyColumn {
                item { SectionTitle("Legend") }
                items(items, key = { it.category }) { item ->
                    LegendRow(item) { visible -> run { it.setVisibility(item.category, visible) } }
                }

                item {
                    Text(
                        "Categories are matched by vector tile source layer, so they work across MapTiler styles.",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                    HorizontalDivider()
                    SectionTitle("Layer queries")
                    SwitchRow("All labels", labelsVisible) { visible ->
                        run { it.setVisibility(labelsQuery, visible) }
                    }
                    SwitchRow("Dim buildings", buildingsDimmed) { dimmed ->
                        buildingsDimmed = dimmed
                        val opacity = PropertyValue.Num(if (dimmed) 0.25 else 1.0)
                        run {
                            it.setPaintProperty(
                                MTLayerQuery(types = setOf(MTLayerType.FILL), categories = setOf(MTLayerCategory.BUILDINGS)),
                                "fill-opacity",
                                opacity,
                            )
                            it.setPaintProperty(
                                MTLayerQuery(
                                    types = setOf(MTLayerType.FILL_EXTRUSION),
                                    categories = setOf(MTLayerCategory.BUILDINGS),
                                ),
                                "fill-extrusion-opacity",
                                opacity,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        color = Color.Gray,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
    )
}

@Composable
private fun LegendRow(
    item: MTLegendItem,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .size(24.dp)
                .background(parseCssColor(item.color) ?: Color.LightGray, RoundedCornerShape(4.dp))
                .border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(4.dp)),
        )
        Column(Modifier.weight(1f)) {
            Text(item.name, color = Color.Black)
            Text("${item.layerIds.size} layers", color = Color.Gray, fontSize = 12.sp)
        }
        Switch(checked = item.isVisible, onCheckedChange = onToggle)
    }
}

@Composable
private fun SwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, color = Color.Black, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

/**
 * Parses the CSS colors used in MapTiler styles: `#rgb`, `#rrggbb`, `#rrggbbaa`, `rgb()`, `rgba()`, `hsl()` and
 * `hsla()`.
 */
internal fun parseCssColor(css: String?): Color? {
    val value = css?.trim()?.lowercase() ?: return null

    if (value.startsWith("#")) {
        var hex = value.drop(1)
        if (hex.length == 3) hex = hex.map { "$it$it" }.joinToString("")
        val number = hex.toLongOrNull(16) ?: return null
        return when (hex.length) {
            6 -> Color(0xFF000000 or number)
            8 -> Color(((number and 0xFF) shl 24) or (number shr 8))
            else -> null
        }
    }

    val open = value.indexOf('(')
    val close = value.lastIndexOf(')')
    if (open < 0 || close < open) return null

    val parts =
        value
            .substring(open + 1, close)
            .split(',', ' ', '/')
            .filter { it.isNotBlank() }
            .map { it.removeSuffix("%").toFloatOrNull() ?: 0f }
    if (parts.size < 3) return null
    val alpha = (parts.getOrNull(3) ?: 1f).coerceIn(0f, 1f)

    return when (value.substring(0, open)) {
        "rgb", "rgba" -> Color(parts[0] / 255f, parts[1] / 255f, parts[2] / 255f, alpha)
        "hsl", "hsla" ->
            Color.hsl(
                hue = ((parts[0] % 360f) + 360f) % 360f,
                saturation = (parts[1] / 100f).coerceIn(0f, 1f),
                lightness = (parts[2] / 100f).coerceIn(0f, 1f),
                alpha = alpha,
            )
        else -> null
    }
}
