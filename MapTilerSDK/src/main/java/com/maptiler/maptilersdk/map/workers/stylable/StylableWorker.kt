/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.stylable

import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType
import com.maptiler.maptilersdk.commands.annotations.AddMarker
import com.maptiler.maptilersdk.commands.annotations.AddTextPopup
import com.maptiler.maptilersdk.commands.annotations.RemoveMarker
import com.maptiler.maptilersdk.commands.annotations.RemoveTextPopup
import com.maptiler.maptilersdk.commands.style.AddLayer
import com.maptiler.maptilersdk.commands.style.AddSource
import com.maptiler.maptilersdk.commands.style.IsSourceLoaded
import com.maptiler.maptilersdk.commands.style.RemoveLayer
import com.maptiler.maptilersdk.commands.style.RemoveSource
import com.maptiler.maptilersdk.commands.style.SetDataToSource
import com.maptiler.maptilersdk.commands.style.SetTilesToSource
import com.maptiler.maptilersdk.commands.style.SetUrlToSource
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.source.MTSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.URL

internal class StylableWorker(
    private val bridge: MTBridge,
    private val scope: CoroutineScope,
) : MTStylable {
    override fun addMarker(marker: MTMarker) {
        scope.launch {
            bridge.execute(
                AddMarker(marker),
            )
        }
    }

    override fun removeMarker(marker: MTMarker) {
        scope.launch {
            bridge.execute(
                RemoveMarker(marker),
            )
        }
    }

    override fun addTextPopup(popup: MTTextPopup) {
        scope.launch {
            bridge.execute(
                AddTextPopup(popup),
            )
        }
    }

    override fun removeTextPopup(popup: MTTextPopup) {
        scope.launch {
            bridge.execute(
                RemoveTextPopup(popup),
            )
        }
    }

    fun addLayer(layer: MTLayer) {
        scope.launch {
            bridge.execute(
                AddLayer(layer),
            )
        }
    }

    fun removeLayer(layer: MTLayer) {
        scope.launch {
            bridge.execute(
                RemoveLayer(layer),
            )
        }
    }

    fun addSource(source: MTSource) {
        scope.launch {
            bridge.execute(
                AddSource(source),
            )
        }
    }

    fun removeSource(source: MTSource) {
        scope.launch {
            bridge.execute(
                RemoveSource(source),
            )
        }
    }

    fun setUrlToSource(
        url: URL,
        source: MTSource,
    ) {
        scope.launch {
            bridge.execute(
                SetUrlToSource(url, source),
            )
        }
    }

    fun setTilesToSource(
        tiles: Array<URL>,
        source: MTSource,
    ) {
        scope.launch {
            bridge.execute(
                SetTilesToSource(tiles, source),
            )
        }
    }

    fun setDataToSource(
        data: URL,
        source: MTSource,
    ) {
        scope.launch {
            bridge.execute(
                SetDataToSource(data, source),
            )
        }
    }

    suspend fun isSourceLoaded(sourceId: String): Boolean {
        val returnTypeValue =
            bridge.execute(
                IsSourceLoaded(sourceId),
            )

        return when (returnTypeValue) {
            is MTBridgeReturnType.BoolValue -> returnTypeValue.value
            is MTBridgeReturnType.DoubleValue -> returnTypeValue.value != 0.0
            is MTBridgeReturnType.StringValue -> if (returnTypeValue.value == "true") true else false
            else -> false
        }
    }
}
