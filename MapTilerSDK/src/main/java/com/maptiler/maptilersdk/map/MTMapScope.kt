/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.maptiler.maptilersdk.annotations.MTAnchor
import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.bridge.MTError
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger
import com.maptiler.maptilersdk.map.style.MTStyle
import com.maptiler.maptilersdk.map.style.MTStyleError
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.source.MTSource

/**
 * Scope for declaring map content inside [MTMapView] using Compose syntax.
 *
 * Entities declared in this scope are added to the map once it is ready
 * ([com.maptiler.maptilersdk.events.MTEvent.ON_READY]) and removed automatically when they leave the composition.
 *
 * Entities are added in declaration order and removed in reverse order, so declare a [MTMapSource]
 * before the [MTMapLayer]s that reference it.
 *
 * Style changes: [MTStyle.setStyle] removes all sources and layers from the map, including those declared
 * with [MTMapSource] and [MTMapLayer], and they are not re-added automatically. Markers and popups are kept.
 * To restore them, remove them from the composition when changing the style and declare them again once
 * the new style has loaded, e.g. after [com.maptiler.maptilersdk.events.MTEvent.ON_STYLE_UPDATE]
 * (requires [com.maptiler.maptilersdk.map.options.MTEventLevel.ALL]):
 * ```
 * var isStyleReady by remember { mutableStateOf(true) }
 *
 * // When changing the style:
 * isStyleReady = false
 * controller.style?.setStyle(MTMapReferenceStyle.SATELLITE)
 *
 * // Once the new style has loaded:
 * isStyleReady = true
 *
 * MTMapView(referenceStyle = MTMapReferenceStyle.STREETS, controller = controller) {
 *     if (isStyleReady) {
 *         MTMapSource(source)
 *         MTMapLayer(layer)
 *     }
 * }
 * ```
 */
@Stable
class MTMapScope internal constructor(
    /**
     * Controller of the map this scope belongs to.
     */
    val controller: MTMapViewController,
    private val isReadyState: State<Boolean>,
) {
    /**
     * Boolean indicating whether the map is ready to accept content.
     */
    val isMapReady: Boolean
        get() = isReadyState.value

    /**
     * Declares a marker on the map.
     *
     * The marker is added when the map is ready and removed when it leaves the composition.
     * Changes to [position] and [draggable] update the existing marker, while changes to [color] or [icon]
     * recreate it.
     *
     * @param position Position of the marker.
     * @param color Color of the marker.
     * @param icon Optional custom icon for the marker. Overrides [color] when set.
     * @param draggable Boolean indicating whether the marker can be dragged.
     * @param onClick Optional callback triggered when the marker is clicked.
     */
    @Suppress("FunctionName")
    @Composable
    fun MTMapMarker(
        position: LngLat,
        color: Color = Color.Blue,
        icon: Bitmap? = null,
        draggable: Boolean = false,
        onClick: ((MTMarker) -> Unit)? = null,
    ) {
        val currentPosition by rememberUpdatedState(position)
        val currentDraggable by rememberUpdatedState(draggable)
        val currentOnClick by rememberUpdatedState(onClick)
        var marker by remember { mutableStateOf<MTMarker?>(null) }
        val style = controller.style

        DisposableEffect(isMapReady, style, color, icon) {
            if (!isMapReady || style == null) {
                return@DisposableEffect onDispose {}
            }

            val newMarker =
                MTMarker(currentPosition, color.toArgb(), icon, currentDraggable).apply {
                    this.onClick = { currentOnClick?.invoke(it) }
                }
            style.addMarker(newMarker)
            marker = newMarker

            onDispose {
                marker = null
                style.removeMarker(newMarker)
            }
        }

        LaunchedEffect(marker, position) {
            val current = marker ?: return@LaunchedEffect
            if (current.coordinates != position) {
                current.setCoordinates(position, controller)
            }
        }

        LaunchedEffect(marker, draggable) {
            val current = marker ?: return@LaunchedEffect
            if (current.draggable != draggable) {
                current.setDraggable(draggable, controller)
            }
        }
    }

    /**
     * Declares a text popup on the map.
     *
     * The popup is added when the map is ready and removed when it leaves the composition.
     * Changes to [position], [text], [offset], [maxWidth] and [anchor] update the existing popup.
     *
     * @param position Position of the popup.
     * @param text Text content of the popup.
     * @param offset Pixel distance from the popup's position.
     * @param maxWidth Optional max width of the popup container in pixels.
     * @param anchor Optional anchor used to align the popup relative to its position.
     */
    @Suppress("FunctionName")
    @Composable
    fun MTMapTextPopup(
        position: LngLat,
        text: String,
        offset: Double = 0.0,
        maxWidth: Int? = null,
        anchor: MTAnchor? = null,
    ) {
        val currentPosition by rememberUpdatedState(position)
        val currentText by rememberUpdatedState(text)
        val currentOffset by rememberUpdatedState(offset)
        val currentMaxWidth by rememberUpdatedState(maxWidth)
        val currentAnchor by rememberUpdatedState(anchor)
        var popup by remember { mutableStateOf<MTTextPopup?>(null) }
        val style = controller.style

        DisposableEffect(isMapReady, style) {
            if (!isMapReady || style == null) {
                return@DisposableEffect onDispose {}
            }

            val newPopup =
                MTTextPopup(currentPosition, currentText, currentOffset, currentMaxWidth).apply {
                    this.anchor = currentAnchor
                }
            style.addTextPopup(newPopup)
            popup = newPopup

            onDispose {
                popup = null
                style.removeTextPopup(newPopup)
            }
        }

        LaunchedEffect(popup, position) {
            val current = popup ?: return@LaunchedEffect
            if (current.coordinates != position) {
                current.setCoordinates(position, controller)
            }
        }

        LaunchedEffect(popup, text) {
            val current = popup ?: return@LaunchedEffect
            if (current.text != text) {
                current.setText(text, controller)
            }
        }

        LaunchedEffect(popup, offset) {
            val current = popup ?: return@LaunchedEffect
            if (current.offset != offset) {
                current.setOffset(offset, controller)
            }
        }

        LaunchedEffect(popup, maxWidth) {
            val current = popup ?: return@LaunchedEffect
            if (maxWidth != null && current.maxWidth != maxWidth) {
                current.setMaxWidth(maxWidth, controller)
            }
        }

        LaunchedEffect(popup, anchor) {
            val current = popup ?: return@LaunchedEffect
            if (current.anchor != anchor) {
                current.setAnchor(anchor, controller)
            }
        }
    }

    /**
     * Declares a source on the map.
     *
     * The source is added when the map is ready and removed when it leaves the composition.
     * Passing a different source instance replaces the previous one.
     * Not re-added automatically after [MTStyle.setStyle], see [MTMapScope] for how to restore the source.
     *
     * @param source Source to add.
     */
    @Suppress("FunctionName")
    @Composable
    fun MTMapSource(source: MTSource) {
        val style = controller.style

        DisposableEffect(isMapReady, style, source) {
            if (!isMapReady || style == null) {
                return@DisposableEffect onDispose {}
            }

            val isAttached = style.attachSource(source)

            onDispose {
                if (isAttached) {
                    style.detachSource(source)
                }
            }
        }
    }

    /**
     * Declares a layer on the map.
     *
     * The layer is added when the map is ready and removed when it leaves the composition.
     * Passing a different layer instance replaces the previous one. Unless it is a background layer,
     * its source must be added first, e.g. with a preceding [MTMapSource].
     * Not re-added automatically after [MTStyle.setStyle], see [MTMapScope] for how to restore the layer.
     *
     * @param layer Layer to add.
     */
    @Suppress("FunctionName")
    @Composable
    fun MTMapLayer(layer: MTLayer) {
        val style = controller.style

        DisposableEffect(isMapReady, style, layer) {
            if (!isMapReady || style == null) {
                return@DisposableEffect onDispose {}
            }

            val isAttached = style.attachLayer(layer)

            onDispose {
                if (isAttached) {
                    style.detachLayer(layer)
                }
            }
        }
    }
}

/**
 * Adds the source and returns true if it was added by this call.
 */
internal fun MTStyle.attachSource(source: MTSource): Boolean =
    try {
        addSource(source)
        true
    } catch (e: MTStyleError.SourceAlreadyExists) {
        MTLogger.log("MTMapScope: source '${source.identifier}' already exists, skipping.", MTLogType.WARNING)
        false
    }

/**
 * Removes the source, ignoring sources already cleared by a style change.
 */
internal fun MTStyle.detachSource(source: MTSource) {
    try {
        removeSource(source)
    } catch (e: MTStyleError.SourceNotFound) {
        MTLogger.log("MTMapScope: source '${source.identifier}' not found on removal.", MTLogType.INFO)
    }
}

/**
 * Adds the layer and returns true if it was added by this call.
 */
internal fun MTStyle.attachLayer(layer: MTLayer): Boolean =
    try {
        addLayer(layer)
        true
    } catch (e: MTStyleError.LayerAlreadyExists) {
        MTLogger.log("MTMapScope: layer '${layer.identifier}' already exists, skipping.", MTLogType.WARNING)
        false
    } catch (e: MTError.MissingParent) {
        MTLogger.log("MTMapScope: source of layer '${layer.identifier}' is missing, skipping.", MTLogType.ERROR)
        false
    }

/**
 * Removes the layer, ignoring layers already cleared by a style change.
 */
internal fun MTStyle.detachLayer(layer: MTLayer) {
    try {
        removeLayer(layer)
    } catch (e: MTStyleError.LayerNotFound) {
        MTLogger.log("MTMapScope: layer '${layer.identifier}' not found on removal.", MTLogType.INFO)
    }
}
