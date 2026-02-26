/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.annotations

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.maptiler.maptilersdk.events.MTEvent
import com.maptiler.maptilersdk.map.LngLat
import com.maptiler.maptilersdk.map.MTMapViewContentDelegate
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.types.MTData
import com.maptiler.maptilersdk.map.types.MTPoint
import com.maptiler.maptilersdk.map.types.MTProjectionType
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Compose-based custom annotation view that can be overlaid on top of MTMapView.
 *
 * Usage: Place this composable in the same `Box` (overlay) as your `MTMapView` so it can
 * position itself using absolute pixel offsets.
 *
 * Example:
 *
 * Box(Modifier.fillMaxSize()) {
 *   MTMapView(referenceStyle, options, controller, Modifier.fillMaxSize())
 *   MTCustomAnnotationView(controller, LngLat(16.6, 49.2)) { YourComposable() }
 * }
 *
 * Requirements: This view relies on `ON_MOVE` and `ON_ZOOM` events.
 * Set `MTMapOptions.eventLevel` to `CAMERA_ONLY` (recommended) or `ALL` so these events are forwarded.
 *
 * @param controller The map controller used to project coordinates and observe events.
 * @param coordinates Geographic location of this annotation.
 * @param offset Pixel offset from the projected point (x to the right, y down).
 * @param anchor Which point of the content should align with the projected point.
 * @param modifier Additional modifiers applied to the positioned Box that wraps [content].
 * @param content The composable content to render as the custom annotation.
 */
@Suppress("FunctionName")
@Composable
fun MTCustomAnnotationView(
    controller: MTMapViewController,
    coordinates: LngLat,
    offset: MTPoint = MTPoint(0.0, 0.0),
    anchor: Alignment = Alignment.Center,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val positionPx = remember { mutableStateOf(IntOffset.Zero) }
    val contentSizePx = remember { mutableStateOf(IntSize.Zero) }
    val isVisible = remember { mutableStateOf(true) }
    val density = LocalDensity.current.density

    fun anchorAdjustment(size: IntSize): IntOffset =
        when (anchor) {
            Alignment.Center -> IntOffset(size.width / 2, size.height / 2)
            Alignment.TopStart -> IntOffset(0, 0)
            Alignment.TopCenter -> IntOffset(size.width / 2, 0)
            Alignment.TopEnd -> IntOffset(size.width, 0)
            Alignment.CenterStart -> IntOffset(0, size.height / 2)
            Alignment.CenterEnd -> IntOffset(size.width, size.height / 2)
            Alignment.BottomStart -> IntOffset(0, size.height)
            Alignment.BottomCenter -> IntOffset(size.width / 2, size.height)
            Alignment.BottomEnd -> IntOffset(size.width, size.height)
            else -> IntOffset(size.width / 2, size.height / 2)
        }

    fun recalculatePosition() {
        scope.launch {
            val projectedDeferred = async { controller.project(coordinates) }
            val boundsDeferred = async { controller.getBounds() }
            val projDeferred = async { controller.style?.getProjection() ?: MTProjectionType.MERCATOR }

            val projected = projectedDeferred.await()
            val bounds = boundsDeferred.await()
            val proj = projDeferred.await()

            val visible =
                if (proj == MTProjectionType.GLOBE) {
                    bounds.contains(coordinates)
                } else {
                    true
                }

            if (isVisible.value != visible) {
                isVisible.value = visible
            }

            if (!visible) return@launch

            val x = ((projected.x + offset.x) * density).roundToInt()
            val y = ((projected.y + offset.y) * density).roundToInt()
            val originX = controller.mapContainerOriginXPx
            val originY = controller.mapContainerOriginYPx
            val adj = anchorAdjustment(contentSizePx.value)
            positionPx.value = IntOffset(originX + x - adj.x, originY + y - adj.y)
        }
    }

    // Initial projection or when coordinates/offset change
    LaunchedEffect(coordinates, offset) {
        recalculatePosition()
    }

    // Listen to map movement/zoom/rotate/pitch/render/resize to keep position in sync
    DisposableEffect(Unit) {
        val contentDelegate =
            object : MTMapViewContentDelegate {
                override fun onEvent(
                    event: MTEvent,
                    data: MTData?,
                ) {
                    when (event) {
                        MTEvent.ON_MOVE,
                        MTEvent.ON_ZOOM,
                        -> recalculatePosition()
                        else -> Unit
                    }
                }
            }

        controller.addContentDelegate(contentDelegate)
        recalculatePosition()

        onDispose {
            controller.removeContentDelegate(contentDelegate)
        }
    }

    Box(
        modifier =
            modifier
                .onGloballyPositioned { coords ->
                    val newSize = coords.size
                    if (newSize != contentSizePx.value) {
                        contentSizePx.value = newSize
                        recalculatePosition()
                    }
                }.graphicsLayer {
                    translationX = positionPx.value.x.toFloat()
                    translationY = positionPx.value.y.toFloat()
                    alpha = if (isVisible.value) 1f else 0f
                },
    ) {
        content()
    }
}
