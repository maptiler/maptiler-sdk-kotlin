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
import com.maptiler.maptilersdk.colorramp.MTArrayColorRampStop
import com.maptiler.maptilersdk.colorramp.MTBuiltinColorRamp
import com.maptiler.maptilersdk.colorramp.MTColorRamp
import com.maptiler.maptilersdk.colorramp.MTColorRampOptions
import com.maptiler.maptilersdk.commands.annotations.AddMarker
import com.maptiler.maptilersdk.commands.annotations.AddMarkers
import com.maptiler.maptilersdk.commands.annotations.AddTextPopup
import com.maptiler.maptilersdk.commands.annotations.RemoveMarker
import com.maptiler.maptilersdk.commands.annotations.RemoveMarkers
import com.maptiler.maptilersdk.commands.annotations.RemoveTextPopup
import com.maptiler.maptilersdk.commands.colorramp.CreateColorRamp
import com.maptiler.maptilersdk.commands.colorramp.CreateColorRampFromArrayDefinition
import com.maptiler.maptilersdk.commands.colorramp.CreateColorRampFromCollection
import com.maptiler.maptilersdk.commands.helpers.AddHeatmapLayer
import com.maptiler.maptilersdk.commands.helpers.AddPointLayer
import com.maptiler.maptilersdk.commands.helpers.AddPolylineLayer
import com.maptiler.maptilersdk.commands.misc.AddLogoControl
import com.maptiler.maptilersdk.commands.style.AddImage
import com.maptiler.maptilersdk.commands.style.AddLayer
import com.maptiler.maptilersdk.commands.style.AddLayers
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
import com.maptiler.maptilersdk.commands.style.GetFilter
import com.maptiler.maptilersdk.commands.style.GetGlyphs
import com.maptiler.maptilersdk.commands.style.GetIdForReferenceStyle
import com.maptiler.maptilersdk.commands.style.GetIdForStyleVariant
import com.maptiler.maptilersdk.commands.style.GetLayerProperty
import com.maptiler.maptilersdk.commands.style.GetLayersOrder
import com.maptiler.maptilersdk.commands.style.GetLight
import com.maptiler.maptilersdk.commands.style.GetNameForReferenceStyle
import com.maptiler.maptilersdk.commands.style.GetNameForStyleVariant
import com.maptiler.maptilersdk.commands.style.GetProjection
import com.maptiler.maptilersdk.commands.style.GetSky
import com.maptiler.maptilersdk.commands.style.GetSource
import com.maptiler.maptilersdk.commands.style.GetSprite
import com.maptiler.maptilersdk.commands.style.GetStyle
import com.maptiler.maptilersdk.commands.style.GetStyleLayers
import com.maptiler.maptilersdk.commands.style.GetTerrain
import com.maptiler.maptilersdk.commands.style.GetTerrainExaggeration
import com.maptiler.maptilersdk.commands.style.HasImage
import com.maptiler.maptilersdk.commands.style.HasTerrain
import com.maptiler.maptilersdk.commands.style.IsGlobeProjection
import com.maptiler.maptilersdk.commands.style.IsLanguageUpdated
import com.maptiler.maptilersdk.commands.style.IsSourceLoaded
import com.maptiler.maptilersdk.commands.style.IsStyleLoaded
import com.maptiler.maptilersdk.commands.style.LayerPropertyKind
import com.maptiler.maptilersdk.commands.style.ListImages
import com.maptiler.maptilersdk.commands.style.MoveLayer
import com.maptiler.maptilersdk.commands.style.PlayVideoSourceById
import com.maptiler.maptilersdk.commands.style.RemoveImage
import com.maptiler.maptilersdk.commands.style.RemoveLayer
import com.maptiler.maptilersdk.commands.style.RemoveLayerById
import com.maptiler.maptilersdk.commands.style.RemoveLayers
import com.maptiler.maptilersdk.commands.style.RemoveSource
import com.maptiler.maptilersdk.commands.style.RemoveSourceById
import com.maptiler.maptilersdk.commands.style.RemoveSprite
import com.maptiler.maptilersdk.commands.style.SetCoordinatesToImageSource
import com.maptiler.maptilersdk.commands.style.SetCoordinatesToVideoSource
import com.maptiler.maptilersdk.commands.style.SetDataToSource
import com.maptiler.maptilersdk.commands.style.SetFilter
import com.maptiler.maptilersdk.commands.style.SetGlyphs
import com.maptiler.maptilersdk.commands.style.SetHalo
import com.maptiler.maptilersdk.commands.style.SetLanguage
import com.maptiler.maptilersdk.commands.style.SetLayerZoomRange
import com.maptiler.maptilersdk.commands.style.SetLayoutProperty
import com.maptiler.maptilersdk.commands.style.SetLight
import com.maptiler.maptilersdk.commands.style.SetPaintProperty
import com.maptiler.maptilersdk.commands.style.SetPropertyForLayers
import com.maptiler.maptilersdk.commands.style.SetRenderWorldCopies
import com.maptiler.maptilersdk.commands.style.SetSecondaryLanguage
import com.maptiler.maptilersdk.commands.style.SetSky
import com.maptiler.maptilersdk.commands.style.SetSpace
import com.maptiler.maptilersdk.commands.style.SetSprite
import com.maptiler.maptilersdk.commands.style.SetStyle
import com.maptiler.maptilersdk.commands.style.SetTilesToSource
import com.maptiler.maptilersdk.commands.style.SetUrlToSource
import com.maptiler.maptilersdk.commands.style.StopVideoSourceById
import com.maptiler.maptilersdk.commands.style.UpdateImage
import com.maptiler.maptilersdk.commands.style.UpdateImageSource
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerOptions
import com.maptiler.maptilersdk.helpers.MTPointLayerOptions
import com.maptiler.maptilersdk.helpers.MTPolygonLayerOptions
import com.maptiler.maptilersdk.helpers.MTPolylineLayerOptions
import com.maptiler.maptilersdk.map.options.MTHalo
import com.maptiler.maptilersdk.map.options.MTSky
import com.maptiler.maptilersdk.map.options.MTSpace
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import com.maptiler.maptilersdk.map.style.MTStyleLayerInfo
import com.maptiler.maptilersdk.map.style.MTStyleLayerInfoSurrogate
import com.maptiler.maptilersdk.map.style.dsl.PropertyValue
import com.maptiler.maptilersdk.map.style.image.MTAddImageOptions
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.source.MTImageSource
import com.maptiler.maptilersdk.map.style.source.MTSource
import com.maptiler.maptilersdk.map.style.source.MTVideoSource
import com.maptiler.maptilersdk.map.types.MTLanguage
import com.maptiler.maptilersdk.map.types.MTProjectionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import java.net.URL

internal class StylableWorker(
    private val bridge: MTBridge,
    private val scope: CoroutineScope,
) : MTStylable {
    override fun addMarker(marker: MTMarker) {
        marker.bindBridge(bridge)
        marker.popup?.bindBridge(bridge)
        marker.popup?.setOpenState(false)
        scope.launch {
            bridge.execute(
                AddMarker(marker),
            )
        }
    }

    override fun addMarkers(
        markers: List<MTMarker>,
        withSingleIcon: Bitmap?,
    ) {
        markers.forEach {
            it.bindBridge(bridge)
            it.popup?.bindBridge(bridge)
            it.popup?.setOpenState(false)
        }
        scope.launch {
            bridge.execute(
                AddMarkers(markers, withSingleIcon),
            )
        }
    }

    fun addLogoControl(id: String = java.util.UUID.randomUUID().toString()) {
        scope.launch {
            bridge.execute(AddLogoControl(id))
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

    fun setSecondaryLanguage(language: MTLanguage) {
        scope.launch {
            bridge.execute(SetSecondaryLanguage(language))
        }
    }

    fun setSprite(spriteUrl: String) {
        scope.launch {
            bridge.execute(SetSprite(spriteUrl))
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

    fun setSky(sky: MTSky) {
        scope.launch {
            bridge.execute(SetSky(sky))
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

    override fun removeMarkers(markers: List<MTMarker>) {
        scope.launch {
            bridge.execute(
                RemoveMarkers(markers),
            )
        }
    }

    override fun addTextPopup(popup: MTTextPopup) {
        popup.bindBridge(bridge)
        popup.setOpenState(true)
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
        popup.setOpenState(false)
    }

    fun addLayer(layer: MTLayer) {
        scope.launch {
            bridge.execute(
                AddLayer(layer),
            )
        }
    }

    fun addLayers(layers: List<MTLayer>) {
        scope.launch {
            bridge.execute(
                AddLayers(layers),
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

    fun updateImage(
        identifier: String,
        image: Bitmap,
    ) {
        scope.launch {
            bridge.execute(
                UpdateImage(identifier, image),
            )
        }
    }

    suspend fun hasImage(identifier: String): Boolean {
        val result = bridge.execute(HasImage(identifier))
        if (result is MTBridgeReturnType.BoolValue) return result.value
        if (result is MTBridgeReturnType.StringValue) return result.value == "true"
        return false
    }

    suspend fun listImages(): List<String> {
        val result = bridge.execute(ListImages())
        if (result is MTBridgeReturnType.StringArray) return result.value
        if (result is MTBridgeReturnType.StringValue) {
            return try {
                JsonConfig.json.decodeFromString<List<String>>(result.value)
            } catch (e: Exception) {
                emptyList()
            }
        }
        return emptyList()
    }

    fun removeImage(name: String) {
        scope.launch {
            bridge.execute(
                RemoveImage(name),
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

    fun addHeatmapLayer(options: MTHeatmapLayerOptions) {
        scope.launch {
            bridge.execute(
                AddHeatmapLayer(options),
            )
        }
    }

    fun addPolylineLayer(options: MTPolylineLayerOptions) {
        scope.launch {
            bridge.execute(
                AddPolylineLayer(options),
            )
        }
    }

    fun addPolygonLayer(options: MTPolygonLayerOptions) {
        scope.launch {
            bridge.execute(
                com.maptiler.maptilersdk.commands.helpers.AddPolygonLayer(options),
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

    fun removeSprite(id: String) {
        scope.launch {
            bridge.execute(
                RemoveSprite(id),
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

    fun removeLayers(layers: List<MTLayer>) {
        scope.launch {
            bridge.execute(
                RemoveLayers(layers),
            )
        }
    }

    fun removeLayerById(layerId: String) {
        scope.launch {
            bridge.execute(RemoveLayerById(layerId))
        }
    }

    fun moveLayer(
        id: String,
        beforeId: String? = null,
    ) {
        scope.launch {
            bridge.execute(MoveLayer(id, beforeId))
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

    fun removeSourceById(sourceId: String) {
        scope.launch {
            bridge.execute(RemoveSourceById(sourceId))
        }
    }

    fun setLayerZoomRange(
        layerId: String,
        minzoom: Double,
        maxzoom: Double,
    ) {
        scope.launch { bridge.execute(SetLayerZoomRange(layerId, minzoom, maxzoom)) }
    }

    suspend fun setLayerZoomRangeAwait(
        layerId: String,
        minzoom: Double,
        maxzoom: Double,
    ) {
        bridge.execute(SetLayerZoomRange(layerId, minzoom, maxzoom))
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

    fun setFeatureState(
        feature: com.maptiler.maptilersdk.map.types.MTFeatureIdentifier,
        stateJson: String,
    ) {
        scope.launch { bridge.execute(com.maptiler.maptilersdk.commands.style.SetFeatureState(feature, stateJson)) }
    }

    suspend fun getFeatureState(feature: com.maptiler.maptilersdk.map.types.MTFeatureIdentifier): JsonElement? {
        val returnTypeValue = bridge.execute(com.maptiler.maptilersdk.commands.style.GetFeatureState(feature))
        return if (returnTypeValue is MTBridgeReturnType.StringValue) {
            runCatching { JsonConfig.json.decodeFromString<JsonElement>(returnTypeValue.value) }.getOrNull()
        } else {
            null
        }
    }

    fun removeFeatureState(
        feature: com.maptiler.maptilersdk.map.types.MTFeatureIdentifier,
        key: String? = null,
    ) {
        scope.launch { bridge.execute(com.maptiler.maptilersdk.commands.style.RemoveFeatureState(feature, key)) }
    }

    suspend fun getFilter(layerId: String): JsonElement? {
        val returnTypeValue = bridge.execute(GetFilter(layerId))
        return if (returnTypeValue is MTBridgeReturnType.StringValue) {
            runCatching { JsonConfig.json.decodeFromString<JsonElement>(returnTypeValue.value) }.getOrNull()
        } else {
            null
        }
    }

    suspend fun getGlyphs(): String? {
        val returnTypeValue = bridge.execute(GetGlyphs())
        return if (returnTypeValue is MTBridgeReturnType.StringValue) {
            returnTypeValue.value
        } else {
            null
        }
    }

    suspend fun getLayersOrder(): List<String> {
        val returnTypeValue = bridge.execute(GetLayersOrder())
        return if (returnTypeValue is MTBridgeReturnType.StringValue) {
            runCatching { JsonConfig.json.decodeFromString<List<String>>(returnTypeValue.value) }.getOrDefault(emptyList())
        } else {
            emptyList()
        }
    }

    suspend fun getLight(): JsonElement? {
        val returnTypeValue = bridge.execute(GetLight())
        return if (returnTypeValue is MTBridgeReturnType.StringValue) {
            runCatching { JsonConfig.json.decodeFromString<JsonElement>(returnTypeValue.value) }.getOrNull()
        } else {
            null
        }
    }

    suspend fun getStyleLayers(): List<MTStyleLayerInfo> {
        val returnTypeValue = bridge.execute(GetStyleLayers)
        val json = (returnTypeValue as? MTBridgeReturnType.StringValue)?.value ?: return emptyList()

        return runCatching {
            JsonConfig.json.decodeFromString<List<MTStyleLayerInfoSurrogate>>(json).map { it.toInfo() }
        }.getOrDefault(emptyList())
    }

    suspend fun getLayerProperty(
        layerId: String,
        name: String,
        kind: LayerPropertyKind,
    ): JsonElement? {
        val returnTypeValue = bridge.execute(GetLayerProperty(layerId, name, kind))
        val json = (returnTypeValue as? MTBridgeReturnType.StringValue)?.value ?: return null

        return runCatching { JsonConfig.json.parseToJsonElement(json) }
            .getOrNull()
            ?.takeUnless { it is JsonNull }
    }

    suspend fun setPropertyForLayers(
        layerIds: List<String>,
        name: String,
        value: PropertyValue,
        kind: LayerPropertyKind,
    ) {
        if (layerIds.isEmpty()) return
        bridge.execute(SetPropertyForLayers(layerIds, name, value, kind))
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

    fun setCoordinatesToImageSource(
        coordinates: List<com.maptiler.maptilersdk.map.LngLat>,
        source: MTImageSource,
    ) {
        scope.launch {
            bridge.execute(
                SetCoordinatesToImageSource(coordinates, source),
            )
        }
    }

    fun updateImageSource(
        url: URL,
        coordinates: List<com.maptiler.maptilersdk.map.LngLat>,
        source: MTImageSource,
    ) {
        scope.launch {
            bridge.execute(
                UpdateImageSource(url, coordinates, source),
            )
        }
    }

    fun setCoordinatesToVideoSource(
        coordinates: List<com.maptiler.maptilersdk.map.LngLat>,
        source: MTVideoSource,
    ) {
        scope.launch {
            bridge.execute(
                SetCoordinatesToVideoSource(coordinates, source),
            )
        }
    }

    fun playVideoSourceById(sourceId: String) {
        scope.launch {
            bridge.execute(
                PlayVideoSourceById(sourceId),
            )
        }
    }

    fun stopVideoSourceById(sourceId: String) {
        scope.launch {
            bridge.execute(
                StopVideoSourceById(sourceId),
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

    fun enableTerrain(
        exaggerationFactor: Double? = null,
        styleUrl: String = "",
    ) {
        val offlinePath = "/offline/"
        val terrainId = "maptiler-terrain"

        scope.launch {
            if (styleUrl.contains(offlinePath)) {
                // Pack identifier is top level
                val packId = styleUrl.split(offlinePath)[1].substringBefore("/")
                val baseURL = styleUrl.substringBefore(offlinePath)

                val sourceExistsReturn = bridge.execute(com.maptiler.maptilersdk.commands.style.SourceExists(terrainId))
                val hasSource =
                    when (sourceExistsReturn) {
                        is MTBridgeReturnType.BoolValue -> sourceExistsReturn.value
                        is MTBridgeReturnType.StringValue -> sourceExistsReturn.value == "true"
                        else -> false
                    }

                if (!hasSource) {
                    val localURL = "$baseURL/offline/$packId/tiles/$terrainId/{z}/{x}/{y}.webp"
                    val source = com.maptiler.maptilersdk.map.style.source.MTRasterDEMSource(terrainId, arrayOf(URL(localURL)))
                    bridge.execute(com.maptiler.maptilersdk.commands.style.AddSource(source))
                }
                bridge.execute(
                    com.maptiler.maptilersdk.commands.style.SetTerrain(
                        com.maptiler.maptilersdk.map.style.MTTerrainSpecification(terrainId, exaggerationFactor),
                    ),
                )
            } else {
                bridge.execute(
                    EnableTerrain(exaggerationFactor),
                )
            }
        }
    }

    fun disableTerrain() {
        scope.launch {
            bridge.execute(
                DisableTerrain(),
            )
        }
    }

    suspend fun hasTerrain(): Boolean {
        val returnTypeValue = bridge.execute(HasTerrain)
        return (returnTypeValue as? MTBridgeReturnType.BoolValue)?.value ?: false
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

    suspend fun isGlobeProjection(): Boolean {
        val returnTypeValue = bridge.execute(IsGlobeProjection())
        return (returnTypeValue as? MTBridgeReturnType.BoolValue)?.value ?: false
    }

    suspend fun isStyleLoaded(): Boolean {
        val returnTypeValue = bridge.execute(IsStyleLoaded())
        return (returnTypeValue as? MTBridgeReturnType.BoolValue)?.value ?: false
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

    suspend fun getSky(): MTSky? {
        val returnTypeValue = bridge.execute(GetSky())
        val json = (returnTypeValue as? MTBridgeReturnType.StringValue)?.value ?: return null
        return runCatching { JsonConfig.json.decodeFromString<MTSky>(json) }.getOrNull()
    }

    suspend fun getSource(sourceId: String): String? {
        val returnTypeValue = bridge.execute(GetSource(sourceId))
        val json = (returnTypeValue as? MTBridgeReturnType.StringValue)?.value ?: return null
        if (json == "null") return null
        return json
    }

    suspend fun getSprite(): String? {
        val returnTypeValue = bridge.execute(GetSprite())
        val json = (returnTypeValue as? MTBridgeReturnType.StringValue)?.value ?: return null
        if (json == "null") return null
        return json
    }

    suspend fun getStyle(): String? {
        val returnTypeValue = bridge.execute(GetStyle())
        val json = (returnTypeValue as? MTBridgeReturnType.StringValue)?.value ?: return null
        if (json == "null") return null
        return json
    }

    suspend fun getTerrain(): com.maptiler.maptilersdk.map.style.MTTerrainSpecification? {
        val returnTypeValue = bridge.execute(GetTerrain())
        val json = (returnTypeValue as? MTBridgeReturnType.StringValue)?.value ?: return null
        if (json == "null") return null
        return runCatching { JsonConfig.json.decodeFromString<com.maptiler.maptilersdk.map.style.MTTerrainSpecification>(json) }.getOrNull()
    }

    suspend fun getTerrainExaggeration(): Double {
        val returnTypeValue = bridge.execute(GetTerrainExaggeration())
        return when (returnTypeValue) {
            is MTBridgeReturnType.DoubleValue -> returnTypeValue.value
            is MTBridgeReturnType.StringValue -> returnTypeValue.value.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    suspend fun isLanguageUpdated(): Boolean {
        val returnTypeValue = bridge.execute(IsLanguageUpdated())
        return (returnTypeValue as? MTBridgeReturnType.BoolValue)?.value ?: false
    }

    suspend fun createColorRamp(options: MTColorRampOptions = MTColorRampOptions()): MTColorRamp {
        val identifier = MTColorRamp.newIdentifier()
        bridge.execute(CreateColorRamp(identifier, options))
        return MTColorRamp(identifier, bridge)
    }

    suspend fun createColorRampFromArrayDefinition(stops: List<MTArrayColorRampStop>): MTColorRamp {
        val identifier = MTColorRamp.newIdentifier()
        bridge.execute(CreateColorRampFromArrayDefinition(identifier, stops))
        return MTColorRamp(identifier, bridge)
    }

    suspend fun colorRampFromCollection(type: MTBuiltinColorRamp): MTColorRamp {
        val identifier = MTColorRamp.newIdentifier()
        bridge.execute(CreateColorRampFromCollection(identifier, type.jsName))
        return MTColorRamp(identifier, bridge)
    }
}
