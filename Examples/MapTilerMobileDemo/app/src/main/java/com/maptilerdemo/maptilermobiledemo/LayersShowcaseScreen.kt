/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maptiler.maptilersdk.events.MTEvent
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.MTMapViewDelegate
import com.maptiler.maptilersdk.map.style.MTLayerCategory
import com.maptiler.maptilersdk.map.style.MTLayerQuery
import com.maptiler.maptilersdk.map.style.MTLegendItem
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTStyle
import com.maptiler.maptilersdk.map.style.MTStyleLayerInfo
import com.maptiler.maptilersdk.map.style.dsl.PropertyValue
import com.maptiler.maptilersdk.map.style.layer.MTLayerType
import com.maptiler.maptilersdk.map.types.MTData
import kotlinx.coroutines.launch

/**
 * Showcase scenes. Each one applies one SDK feature and shows the exact call and its result.
 */
enum class ShowcaseScene(
    val chipTitle: String,
    val title: String,
    val subtitle: String,
    /** The SDK call shown on screen, formatted to fit the card without wrapping. */
    val code: String,
) {
    LEGEND(
        "Legend",
        "Build a legend",
        "Colors, layer ids and visibility for every category in the style.",
        "val legend = style.getLegend()",
    ),
    HIDE_WATER(
        "Hide water",
        "Toggle a category",
        "One call hides oceans, lakes, rivers and their labels.",
        """
        style.setVisibility(
            MTLayerCategory.WATER, false
        )
        """.trimIndent(),
    ),
    NATURE_ONLY(
        "Nature & water",
        "Combine categories",
        "Hide everything except water, parks, forests and grass.",
        """
        val keep = setOf(NATURE, WATER)
        val others = MTLayerCategory.entries
            .filter { it !in keep }.toSet()
        style.setVisibility(
            MTLayerQuery(categories = others),
            false,
        )
        """.trimIndent(),
    ),
    NO_LABELS(
        "No labels",
        "Query by type",
        "Hide every text and icon layer, regardless of the style.",
        """
        style.setVisibility(
            MTLayerQuery(types = setOf(SYMBOL)),
            false,
        )
        """.trimIndent(),
    ),
    RECOLOR_ROADS(
        "Recolor roads",
        "Restyle layers",
        "Set a paint property on all road lines in one bridge call.",
        """
        style.setPaintProperty(
            MTLayerQuery(
                types = setOf(LINE),
                categories = setOf(ROADS),
            ),
            "line-color",
            PropertyValue.Str("#E4572E"),
        )
        """.trimIndent(),
    ),
    QUERY_LAYERS(
        "Query layers",
        "Inspect layers",
        "List any layer of the style, including built-in MapTiler layers.",
        """
        val layers = style.getStyleLayers(
            MTLayerQuery.of(MTLayerCategory.WATER)
        )
        """.trimIndent(),
    ),
}

/**
 * Holds showcase state and applies scenes. Remembers exactly which layers it hid or recolored, so switching scenes
 * restores them without touching layers the style hides on its own.
 */
class LayersShowcaseState(
    context: Context,
) : MTMapViewDelegate {
    val controller = MTMapViewController(context).apply { delegate = this@LayersShowcaseState }

    var scene by mutableStateOf(ShowcaseScene.LEGEND)
        private set
    var result by mutableStateOf<String?>(null)
        private set
    var legend by mutableStateOf<List<MTLegendItem>>(emptyList())
        private set
    var queriedLayers by mutableStateOf<List<MTStyleLayerInfo>>(emptyList())
        private set
    var isReady by mutableStateOf(false)
        private set
    private var isBusy = false

    private val hiddenLayerIds = mutableSetOf<String>()
    private val originalPaint = mutableMapOf<String, Pair<String, String>>()

    override fun onMapViewInitialized() = Unit

    override fun onEventTriggered(
        event: MTEvent,
        data: MTData?,
    ) {
        if (event == MTEvent.ON_READY) isReady = true
    }

    suspend fun select(newScene: ShowcaseScene) {
        val style = controller.style ?: return
        if (isBusy) return

        isBusy = true
        scene = newScene
        result = null
        try {
            reset(style)
            result = apply(newScene, style)
            if (newScene == ShowcaseScene.LEGEND) legend = style.getLegend()
        } catch (e: Exception) {
            result = "Error: ${e.message}"
        } finally {
            isBusy = false
        }
    }

    suspend fun setCategory(
        item: MTLegendItem,
        visible: Boolean,
    ) {
        val style = controller.style ?: return

        if (visible) {
            val ids = hiddenLayerIds.intersect(item.layerIds.toSet())
            style.setVisibility(MTLayerQuery(ids = ids), true)
            hiddenLayerIds.removeAll(ids)
        } else {
            hide(MTLayerQuery.of(item.category), style)
        }
        legend = style.getLegend()
        val hidden = legend.count { !it.isVisible }
        result = if (hidden == 0) "${legend.size} categories" else "$hidden of ${legend.size} hidden"
    }

    private suspend fun apply(
        scene: ShowcaseScene,
        style: MTStyle,
    ): String =
        when (scene) {
            ShowcaseScene.LEGEND -> "${style.getLegend().size} categories"
            ShowcaseScene.HIDE_WATER -> "${hide(MTLayerQuery.of(MTLayerCategory.WATER), style)} hidden"
            ShowcaseScene.NATURE_ONLY -> {
                val keep = setOf(MTLayerCategory.NATURE, MTLayerCategory.WATER)
                val others = MTLayerCategory.entries.filter { it !in keep }.toSet()
                "${hide(MTLayerQuery(categories = others), style)} hidden"
            }
            ShowcaseScene.NO_LABELS -> "${hide(MTLayerQuery(types = setOf(MTLayerType.SYMBOL)), style)} hidden"
            ShowcaseScene.RECOLOR_ROADS -> {
                val query = MTLayerQuery(types = setOf(MTLayerType.LINE), categories = setOf(MTLayerCategory.ROADS))
                style.getStyleLayers(query).forEach { layer ->
                    val original = style.getPaintProperty(layer.id, LINE_COLOR)?.toString() ?: "null"
                    originalPaint[layer.id] = LINE_COLOR to original
                }
                "${style.setPaintProperty(query, LINE_COLOR, PropertyValue.Str(ROAD_COLOR)).size} restyled"
            }
            ShowcaseScene.QUERY_LAYERS -> {
                queriedLayers = style.getStyleLayers(MTLayerQuery.of(MTLayerCategory.WATER))
                "${queriedLayers.size} matched"
            }
        }

    /** Hides the currently visible layers matching [query] and remembers them for the reset. */
    private suspend fun hide(
        query: MTLayerQuery,
        style: MTStyle,
    ): Int {
        val ids = style.getStyleLayers(query).filter { it.isVisible }.map { it.id }.toSet()
        style.setVisibility(MTLayerQuery(ids = ids), false)
        hiddenLayerIds.addAll(ids)
        return ids.size
    }

    private suspend fun reset(style: MTStyle) {
        if (hiddenLayerIds.isNotEmpty()) {
            style.setVisibility(MTLayerQuery(ids = hiddenLayerIds.toSet()), true)
            hiddenLayerIds.clear()
        }
        originalPaint.forEach { (layerId, paint) ->
            style.setPaintPropertyAwait(layerId, paint.first, PropertyValue.RawJs(paint.second))
        }
        originalPaint.clear()
    }

    private companion object {
        const val LINE_COLOR = "line-color"
        const val ROAD_COLOR = "#E4572E"
    }
}

private val Accent = Color(0xFF3478F6)
private val CodeBackground = Color(0xFF1C2130)
private val CodeText = Color(0xFFDEEBFF)
private val Success = Color(0xFF1E9E4A)

@Suppress("FunctionName")
@Composable
fun LayersShowcaseScreen(
    context: Context,
    onClose: () -> Unit,
) {
    val state = remember(context) { LayersShowcaseState(context) }
    val options = remember { MTMapOptions(center = LngLat(8.545, 47.350), zoom = 12.4) }

    LaunchedEffect(state.isReady) {
        if (state.isReady) state.select(ShowcaseScene.LEGEND)
    }

    Box(Modifier.fillMaxSize()) {
        MTMapView(MTMapReferenceStyle.STREETS, options, state.controller, modifier = Modifier.fillMaxSize())

        Header(onClose, Modifier.align(Alignment.TopStart).statusBarsPadding())

        ShowcaseCard(
            state,
            Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun Header(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(44.dp)
                .shadow(6.dp, CircleShape)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close", tint = Color.Black)
            }
        }
        Box(
            Modifier
                .height(44.dp)
                .shadow(6.dp, RoundedCornerShape(22.dp))
                .background(Color.White, RoundedCornerShape(22.dp))
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("Layers & Legends", color = Color.Black, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ShowcaseCard(
    state: LayersShowcaseState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val chipsState = rememberLazyListState()

    LaunchedEffect(state.scene) {
        chipsState.animateScrollToItem((state.scene.ordinal - 1).coerceAtLeast(0))
    }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LazyRow(state = chipsState, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(ShowcaseScene.entries) { _, scene ->
                val selected = scene == state.scene
                Text(
                    scene.chipTitle,
                    color = if (selected) Color.White else Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier =
                        Modifier
                            .background(if (selected) Accent else Color(0xFFF0F1F5), RoundedCornerShape(50))
                            .clickable(enabled = state.isReady) { scope.launch { state.select(scene) } }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }

        if (!state.isReady) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                Text("Loading map…", color = Color.Gray)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                state.scene.title,
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            state.result?.let { ResultPill(it) }
        }

        Text(state.scene.subtitle, color = Color.Gray, fontSize = 14.sp)

        Text(
            state.scene.code,
            color = CodeText,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.5.sp,
            lineHeight = 16.sp,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(CodeBackground, RoundedCornerShape(12.dp))
                    .padding(12.dp),
        )

        when (state.scene) {
            ShowcaseScene.LEGEND ->
                LegendGrid(state.legend) { item -> scope.launch { state.setCategory(item, !item.isVisible) } }
            ShowcaseScene.QUERY_LAYERS -> QueryList(state.queriedLayers)
            else -> Unit
        }
    }
}

@Composable
private fun ResultPill(text: String) {
    Row(
        modifier =
            Modifier
                .background(Success.copy(alpha = 0.12f), RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Success, modifier = Modifier.size(14.dp))
        Text(text, color = Success, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun LegendGrid(
    items: List<MTLegendItem>,
    onToggle: (MTLegendItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { item ->
                    LegendCell(item, Modifier.weight(1f)) { onToggle(item) }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun LegendCell(
    item: MTLegendItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier.clickable(onClick = onClick).alpha(if (item.isVisible) 1f else 0.45f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier
                .size(18.dp)
                .background(parseCssColor(item.color) ?: Color.LightGray, RoundedCornerShape(4.dp))
                .border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(4.dp)),
        )
        Column(Modifier.weight(1f)) {
            Text(
                item.name,
                color = Color.Black,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (item.isVisible) null else TextDecoration.LineThrough,
            )
            Text("${item.layerIds.size} layers", color = Color.Gray, fontSize = 11.sp, lineHeight = 13.sp)
        }
        Box(
            Modifier
                .size(10.dp)
                .background(if (item.isVisible) Accent else Color.LightGray, CircleShape),
        )
    }
}

@Composable
private fun QueryList(layers: List<MTStyleLayerInfo>) {
    val visibleRows = 5

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        QueryRow("id", "type", "source layer", header = true)
        layers.take(visibleRows).forEach { QueryRow(it.id, it.rawType, it.sourceLayer ?: "–") }
        if (layers.size > visibleRows) {
            Text("+ ${layers.size - visibleRows} more", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun QueryRow(
    id: String,
    type: String,
    sourceLayer: String,
    header: Boolean = false,
) {
    val color = if (header) Color.Gray else Color.Black
    val size = if (header) 11.sp else 12.sp
    val family = if (header) FontFamily.Default else FontFamily.Monospace

    Row {
        Text(id, color = color, fontSize = size, fontFamily = family, maxLines = 1, modifier = Modifier.weight(1f))
        Text(type, color = color, fontSize = size, fontFamily = family, modifier = Modifier.width(52.dp))
        Text(sourceLayer, color = color, fontSize = size, fontFamily = family, modifier = Modifier.width(96.dp))
    }
}
