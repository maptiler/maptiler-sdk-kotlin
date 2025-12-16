/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.stylable

import android.graphics.Bitmap
import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType
import com.maptiler.maptilersdk.commands.annotations.AddMarker
import com.maptiler.maptilersdk.commands.annotations.AddTextPopup
import com.maptiler.maptilersdk.commands.annotations.RemoveMarker
import com.maptiler.maptilersdk.commands.annotations.RemoveTextPopup
import com.maptiler.maptilersdk.commands.helpers.AddPointLayer
import com.maptiler.maptilersdk.commands.misc.AddLogoControl
import com.maptiler.maptilersdk.commands.style.AddImage
import com.maptiler.maptilersdk.commands.style.AddLayer
import com.maptiler.maptilersdk.commands.style.AddSource
import com.maptiler.maptilersdk.commands.style.AddSprite
import com.maptiler.maptilersdk.commands.style.DisableHalo
import com.maptiler.maptilersdk.commands.style.DisableHaloAnimations
import com.maptiler.maptilersdk.commands.style.DisableSpaceAnimations
import com.maptiler.maptilersdk.commands.style.DisableTerrain
import com.maptiler.maptilersdk.commands.style.EnableGlobeProjection
import com.maptiler.maptilersdk.commands.style.EnableHalo
import com.maptiler.maptilersdk.commands.style.EnableMercatorProjection
import com.maptiler.maptilersdk.commands.style.EnableTerrain
import com.maptiler.maptilersdk.commands.style.GetIdForReferenceStyle
import com.maptiler.maptilersdk.commands.style.GetIdForStyleVariant
import com.maptiler.maptilersdk.commands.style.GetNameForReferenceStyle
import com.maptiler.maptilersdk.commands.style.GetNameForStyleVariant
import com.maptiler.maptilersdk.commands.style.GetProjection
import com.maptiler.maptilersdk.commands.style.IsSourceLoaded
import com.maptiler.maptilersdk.commands.style.RemoveLayer
import com.maptiler.maptilersdk.commands.style.RemoveSource
import com.maptiler.maptilersdk.commands.style.SetDataToSource
import com.maptiler.maptilersdk.commands.style.SetFilter
import com.maptiler.maptilersdk.commands.style.SetGlyphs
import com.maptiler.maptilersdk.commands.style.SetHalo
import com.maptiler.maptilersdk.commands.style.SetLanguage
import com.maptiler.maptilersdk.commands.style.SetLayoutProperty
import com.maptiler.maptilersdk.commands.style.SetLight
import com.maptiler.maptilersdk.commands.style.SetPaintProperty
import com.maptiler.maptilersdk.commands.style.SetRenderWorldCopies
import com.maptiler.maptilersdk.commands.style.SetSpace
import com.maptiler.maptilersdk.commands.style.SetStyle
import com.maptiler.maptilersdk.commands.style.SetTilesToSource
import com.maptiler.maptilersdk.commands.style.SetUrlToSource
import com.maptiler.maptilersdk.helpers.MTPointLayerOptions
import com.maptiler.maptilersdk.map.options.MTHalo
import com.maptiler.maptilersdk.map.options.MTSpace
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import com.maptiler.maptilersdk.map.style.dsl.PropertyValue
import com.maptiler.maptilersdk.map.style.image.MTAddImageOptions
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.source.MTSource
import com.maptiler.maptilersdk.map.types.MTLanguage
import com.maptiler.maptilersdk.map.types.MTProjectionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.URL

internal class StylableWorker(
    private val bridge: MTBridge,
    private val scope: CoroutineScope,
) : MTStylable {
    override fun addMarker(marker: MTMarker) {
        marker.bindBridge(bridge)
        scope.launch {
            bridge.execute(
                AddMarker(marker),
            )
        }
    }

    fun addLogoControl() {
        scope.launch {
            bridge.execute(AddLogoControl())
        }
    }

    suspend fun getIdForReferenceStyle(reference: MTMapReferenceStyle): String? {
        val returnTypeValue = bridge.execute(GetIdForReferenceStyle(reference))

        return when (returnTypeValue) {
            is MTBridgeReturnType.StringValue -> returnTypeValue.value.trim('"')
            else -> null
        }
    }

    suspend fun getIdForStyleVariant(variant: MTMapStyleVariant): String? {
        val returnTypeValue = bridge.execute(GetIdForStyleVariant(variant))

        return when (returnTypeValue) {
            is MTBridgeReturnType.StringValue -> returnTypeValue.value.trim('"')
            else -> null
        }
    }

    suspend fun getNameForReferenceStyle(reference: MTMapReferenceStyle): String? {
        val returnTypeValue = bridge.execute(GetNameForReferenceStyle(reference))

        return when (returnTypeValue) {
            is MTBridgeReturnType.StringValue -> returnTypeValue.value.trim('"')
            else -> null
        }
    }

    suspend fun getNameForStyleVariant(variant: MTMapStyleVariant): String? {
        val returnTypeValue = bridge.execute(GetNameForStyleVariant(variant))

        return when (returnTypeValue) {
            is MTBridgeReturnType.StringValue -> returnTypeValue.value.trim('"')
            else -> null
        }
    }

    fun setGlyphs(url: URL) {
        scope.launch {
            bridge.execute(SetGlyphs(url))
        }
    }

    fun setLanguage(language: MTLanguage) {
        scope.launch {
            bridge.execute(SetLanguage(language))
        }
    }

    fun setLight(lightOptionsJson: String) {
        scope.launch {
            bridge.execute(SetLight(lightOptionsJson))
        }
    }

    fun setRenderWorldCopies(shouldRenderWorldCopies: Boolean) {
        scope.launch {
            bridge.execute(SetRenderWorldCopies(shouldRenderWorldCopies))
        }
    }

    fun setSpace(space: MTSpace) {
        scope.launch {
            bridge.execute(SetSpace(space))
        }
    }

    fun setHalo(halo: MTHalo) {
        scope.launch { bridge.execute(SetHalo(halo)) }
    }

    fun enableHalo() {
        scope.launch { bridge.execute(EnableHalo()) }
    }

    fun disableHalo() {
        scope.launch { bridge.execute(DisableHalo()) }
    }

    fun disableHaloAnimations() {
        scope.launch { bridge.execute(DisableHaloAnimations()) }
    }

    fun disableSpaceAnimations() {
        scope.launch { bridge.execute(DisableSpaceAnimations()) }
    }

    fun setStyle(
        referenceStyle: MTMapReferenceStyle,
        styleVariant: MTMapStyleVariant?,
    ) {
        scope.launch {
            bridge.execute(SetStyle(referenceStyle, styleVariant))
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

    fun addImage(
        identifier: String,
        image: Bitmap,
        options: MTAddImageOptions? = null,
    ) {
        scope.launch {
            bridge.execute(
                AddImage(identifier, image, options),
            )
        }
    }

    fun addPointLayer(options: MTPointLayerOptions) {
        scope.launch {
            bridge.execute(
                AddPointLayer(options),
            )
        }
    }

    fun addSprite(
        identifier: String,
        spriteUrl: URL,
    ) {
        scope.launch {
            bridge.execute(
                AddSprite(identifier, spriteUrl),
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

    // Style property setters
    fun setFilter(
        layerId: String,
        filter: PropertyValue,
    ) {
        scope.launch { bridge.execute(SetFilter(layerId, filter)) }
    }

    suspend fun setFilterAwait(
        layerId: String,
        filter: PropertyValue,
    ) {
        bridge.execute(SetFilter(layerId, filter))
    }

    fun setLayoutProperty(
        layerId: String,
        name: String,
        value: PropertyValue,
    ) {
        scope.launch { bridge.execute(SetLayoutProperty(layerId, name, value)) }
    }

    suspend fun setLayoutPropertyAwait(
        layerId: String,
        name: String,
        value: PropertyValue,
    ) {
        bridge.execute(SetLayoutProperty(layerId, name, value))
    }

    fun setPaintProperty(
        layerId: String,
        name: String,
        value: PropertyValue,
    ) {
        scope.launch { bridge.execute(SetPaintProperty(layerId, name, value)) }
    }

    suspend fun setPaintPropertyAwait(
        layerId: String,
        name: String,
        value: PropertyValue,
    ) {
        bridge.execute(SetPaintProperty(layerId, name, value))
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

    fun enableGlobeProjection() {
        scope.launch {
            bridge.execute(
                EnableGlobeProjection(),
            )
        }
    }

    fun enableMercatorProjection() {
        scope.launch {
            bridge.execute(
                EnableMercatorProjection(),
            )
        }
    }

    fun enableTerrain(exaggerationFactor: Double? = null) {
        scope.launch {
            bridge.execute(
                EnableTerrain(exaggerationFactor),
            )
        }
    }

    fun disableTerrain() {
        scope.launch {
            bridge.execute(
                DisableTerrain(),
            )
        }
    }

    suspend fun getProjection(): MTProjectionType {
        val returnTypeValue = bridge.execute(GetProjection())

        return when (returnTypeValue) {
            is MTBridgeReturnType.StringValue ->
                when (returnTypeValue.value.trim('"')) { // Android webview returns quoted strings
                    "mercator" -> MTProjectionType.MERCATOR
                    "globe" -> MTProjectionType.GLOBE
                    else -> MTProjectionType.MERCATOR
                }
            is MTBridgeReturnType.Null, MTBridgeReturnType.UnsupportedType -> MTProjectionType.MERCATOR
            else -> MTProjectionType.MERCATOR
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
