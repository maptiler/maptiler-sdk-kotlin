/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style

import android.graphics.Bitmap
import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTError
import com.maptiler.maptilersdk.colorramp.MTArrayColorRampStop
import com.maptiler.maptilersdk.colorramp.MTColorRamp
import com.maptiler.maptilersdk.colorramp.MTColorRampCollection
import com.maptiler.maptilersdk.colorramp.MTColorRampOptions
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerHelper
import com.maptiler.maptilersdk.helpers.MTHeatmapLayerOptions
import com.maptiler.maptilersdk.helpers.MTPointLayerHelper
import com.maptiler.maptilersdk.helpers.MTPointLayerOptions
import com.maptiler.maptilersdk.helpers.MTPolygonLayerHelper
import com.maptiler.maptilersdk.helpers.MTPolygonLayerOptions
import com.maptiler.maptilersdk.helpers.MTPolylineLayerHelper
import com.maptiler.maptilersdk.helpers.MTPolylineLayerOptions
import com.maptiler.maptilersdk.map.style.dsl.PropertyValue
import com.maptiler.maptilersdk.map.style.image.MTAddImageOptions
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.source.MTSource
import com.maptiler.maptilersdk.map.types.MTLanguage
import com.maptiler.maptilersdk.map.types.MTProjectionType
import com.maptiler.maptilersdk.map.workers.stylable.MTStylable
import com.maptiler.maptilersdk.map.workers.stylable.StylableWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.net.URL

/**
 * The proxy object for the current map style.
 *
 * Set of convenience methods for style, sources and layers manipulation.
 * MTStyle is null until map loading is complete.
 */
class MTStyle(
    reference: MTMapReferenceStyle,
    variant: MTMapStyleVariant? = null,
) : MTStylable {
    private var coroutineScope: CoroutineScope? = null
    private var bridge: MTBridge? = null

    private lateinit var stylableWorker: StylableWorker

    private val mapSources: MutableMap<String, WeakReference<MTSource>> = mutableMapOf()
    private val mapLayers: MutableMap<String, WeakReference<MTLayer>> = mutableMapOf()

    private val queue: MutableList<StyleTask> = mutableListOf()

    /**
     * Current reference style of the map object.
     */
    var referenceStyle: MTMapReferenceStyle = MTMapReferenceStyle.STREETS
        private set

    /**
     * Current style variant of the map object.
     */
    var styleVariant: MTMapStyleVariant? = null
        private set

    init {
        referenceStyle = reference
        styleVariant = variant
    }

    internal fun initWorker(
        bridge: MTBridge,
        coroutineScope: CoroutineScope,
    ) {
        this.bridge = bridge
        this.coroutineScope = coroutineScope

        stylableWorker = StylableWorker(bridge, coroutineScope)
    }

    internal fun processLayersQueueIfNeeded() {
        queue.forEach { it.execute() }
        queue.clear()
    }

    /**
     * Returns variants for the current reference style if they exist.
     */
    fun getVariantsForCurrentReferenceStyle(): List<MTMapStyleVariant>? = this.referenceStyle.getVariants()

    /**
     * Returns variants for the provided reference style if they exist.
     *
     * @param reference Reference style for which to get variants.
     */
    fun getVariantsForReferenceStyle(reference: MTMapReferenceStyle): List<MTMapStyleVariant>? = reference.getVariants()

    // STYLABLE

    /**
     * Adds the marker to the map.
     *
     * @param marker Marker to add.
     */
    override fun addMarker(marker: MTMarker) = stylableWorker.addMarker(marker)

    /**
     * Removes the marker from the map.
     *
     * @param marker Marker to remove.
     */
    override fun removeMarker(marker: MTMarker) = stylableWorker.removeMarker(marker)

    /**
     * Adds a text popup to the map.
     *
     * @param popup Popup to add.
     */
    override fun addTextPopup(popup: MTTextPopup) = stylableWorker.addTextPopup(popup)

    /**
     * Removes a text popup from the map.
     *
     * @param popup Popup to remove.
     */
    override fun removeTextPopup(popup: MTTextPopup) = stylableWorker.removeTextPopup(popup)

    /**
     * Registers an image asset that can be referenced from the style.
     *
     * @param identifier Unique name for the image in the style registry.
     * @param image Bitmap containing the image data.
     * @param options Optional image configuration such as pixel ratio or SDF toggle.
     */
    fun addImage(
        identifier: String,
        image: Bitmap,
        options: MTAddImageOptions? = null,
    ) = stylableWorker.addImage(identifier, image, options)

    /**
     * Registers a sprite hosted at the provided URL under the given identifier.
     *
     * @param identifier Unique name for the sprite resource.
     * @param url Fully qualified URL pointing to the sprite image.
     */
    fun addSprite(
        identifier: String,
        url: URL,
    ) = stylableWorker.addSprite(identifier, url)

    /**
     * Removes a layer by its identifier if present.
     */
    fun removeLayerById(layerId: String) = stylableWorker.removeLayerById(layerId)

    /**
     * Removes a source by its identifier if present.
     */
    fun removeSourceById(sourceId: String) = stylableWorker.removeSourceById(sourceId)

    /**
     * Adds a layer to the map.
     *
     * @param layer Layer to be added.
     * @throws MTStyleError.LayerAlreadyExists if layer with the same id is already added to the map.
     */
    fun addLayer(layer: MTLayer) {
        if (mapLayers.containsKey(layer.identifier)) {
            throw MTStyleError.LayerAlreadyExists
        }

        coroutineScope?.launch {
            mapLayers[layer.identifier] = WeakReference(layer)

            if (mapSources[layer.sourceIdentifier] != null) {
                val isLoaded = isSourceLoaded(layer.sourceIdentifier)

                if (isLoaded) {
                    stylableWorker.addLayer(layer)
                } else {
                    val layerTask =
                        StyleTask(layer.identifier) {
                            stylableWorker.addLayer(layer)
                        }

                    queue.add(layerTask)
                }
            } else {
                throw MTError.MissingParent
            }
        }
    }

    /**
     * Removes a layer from the map.
     *
     * @param layer Layer to be removed.
     * @throws MTStyleError.LayerNotFound if layer does not exist on the map.
     */
    fun removeLayer(layer: MTLayer) {
        if (!mapLayers.containsKey(layer.identifier)) {
            throw MTStyleError.LayerNotFound
        }

        mapLayers.remove(layer.identifier)
        stylableWorker.removeLayer(layer)
    }

    /**
     * Adds a source to the map.
     *
     * @param source Source to be added.
     * @throws MTStyleError.SourceAlreadyExists if source with the same id is already added to the map.
     */
    fun addSource(source: MTSource) {
        if (mapSources.containsKey(source.identifier)) {
            throw MTStyleError.SourceAlreadyExists
        }

        mapSources[source.identifier] = WeakReference(source)
        stylableWorker.addSource(source)
    }

    /**
     * Removes a source from the map.
     *
     * @param source Source to be removed.
     * @throws MTStyleError.SourceNotFound if source does not exist on the map.
     */
    fun removeSource(source: MTSource) {
        if (!mapSources.containsKey(source.identifier)) {
            throw MTStyleError.SourceNotFound
        }

        mapSources.remove(source.identifier)
        stylableWorker.removeSource(source)
    }

    /**
     * Returns boolean value indicating whether the source with provided id is loaded.
     *
     * @param sourceId The id of the source.
     */
    suspend fun isSourceLoaded(sourceId: String): Boolean = stylableWorker.isSourceLoaded(sourceId)

    internal fun setUrlToSource(
        url: URL,
        source: MTSource,
    ) = stylableWorker.setUrlToSource(url, source)

    internal fun setTilesToSource(
        tiles: Array<URL>,
        source: MTSource,
    ) = stylableWorker.setTilesToSource(tiles, source)

    internal fun setDataToSource(
        data: URL,
        source: MTSource,
    ) = stylableWorker.setDataToSource(data, source)

    /**
     * Enables the globe projection visualization.
     */
    fun enableGlobeProjection() = stylableWorker.enableGlobeProjection()

    /**
     * Enables the mercator projection visualization.
     */
    fun enableMercatorProjection() = stylableWorker.enableMercatorProjection()

    /**
     * Enables the 3D terrain visualization.
     *
     * @param exaggerationFactor Optional exaggeration factor to apply when enabling terrain.
     */
    fun enableTerrain(exaggerationFactor: Double? = null) = stylableWorker.enableTerrain(exaggerationFactor)

    /**
     * Disables the 3D terrain visualization.
     */
    fun disableTerrain() = stylableWorker.disableTerrain()

    /**
     * Gets the current projection type. Defaults to [MTProjectionType.MERCATOR] when unavailable.
     */
    suspend fun getProjection(): MTProjectionType = stylableWorker.getProjection()

    // CONTROLS / STYLE HELPERS

    /**
     * Adds the MapTiler logo control to the map.
     */
    fun addLogoControl() = stylableWorker.addLogoControl()

    // Adds a point visualization layer using the helper with the provided options.
    internal fun addPointLayer(options: MTPointLayerOptions) = stylableWorker.addPointLayer(options)

    // Adds a heatmap visualization layer using the helper with the provided options.
    internal fun addHeatmapLayer(options: MTHeatmapLayerOptions) = stylableWorker.addHeatmapLayer(options)

    // Adds a polyline visualization layer using the helper with the provided options.
    internal fun addPolylineLayer(options: MTPolylineLayerOptions) = stylableWorker.addPolylineLayer(options)

    // Adds a polygon visualization layer using the helper with the provided options.
    internal fun addPolygonLayer(options: MTPolygonLayerOptions) = stylableWorker.addPolygonLayer(options)

    /**
     * Returns a helper instance for adding heatmap layers.
     */
    fun heatmapHelper(): MTHeatmapLayerHelper = MTHeatmapLayerHelper(this)

    /**
     * Returns a helper instance for adding point layers.
     */
    fun pointHelper(): MTPointLayerHelper = MTPointLayerHelper(this)

    /**
     * Returns a helper instance for adding polyline layers.
     */
    fun polylineHelper(): MTPolylineLayerHelper = MTPolylineLayerHelper(this)

    /**
     * Returns a helper instance for adding polygon layers.
     */
    fun polygonHelper(): MTPolygonLayerHelper = MTPolygonLayerHelper(this)

    /**
     * Creates a custom color ramp.
     */
    suspend fun createColorRamp(options: MTColorRampOptions = MTColorRampOptions()): MTColorRamp = stylableWorker.createColorRamp(options)

    /**
     * Creates a color ramp from an array definition.
     */
    suspend fun colorRampFromArrayDefinition(stops: List<MTArrayColorRampStop>): MTColorRamp =
        stylableWorker.createColorRampFromArrayDefinition(stops)

    /**
     * Access to the collection of built-in color ramps.
     */
    fun colorRampCollection(): MTColorRampCollection = MTColorRampCollection(stylableWorker)

    /**
     * Returns SDK style id for a given reference style.
     *
     * @param reference Reference style.
     */
    suspend fun getIdForReferenceStyle(reference: MTMapReferenceStyle): String? = stylableWorker.getIdForReferenceStyle(reference)

    /**
     * Returns SDK style id for a given style variant.
     *
     * @param variant Style variant.
     */
    suspend fun getIdForStyleVariant(variant: MTMapStyleVariant): String? = stylableWorker.getIdForStyleVariant(variant)

    /**
     * Returns display name for a given reference style.
     *
     * @param reference Reference style.
     */
    suspend fun getNameForReferenceStyle(reference: MTMapReferenceStyle): String? = stylableWorker.getNameForReferenceStyle(reference)

    /**
     * Returns display name for a given style variant.
     *
     * @param variant Style variant.
     */
    suspend fun getNameForStyleVariant(variant: MTMapStyleVariant): String? = stylableWorker.getNameForStyleVariant(variant)

    /**
     * Sets the glyphs URL template.
     *
     * @param url URL template for glyphs.
     */
    fun setGlyphs(url: java.net.URL) = stylableWorker.setGlyphs(url)

    /**
     * Sets the map language.
     *
     * @param language Desired language of the map labels.
     */
    fun setLanguage(language: MTLanguage) = stylableWorker.setLanguage(language)

    /**
     * Sets the global light options for the current style.
     * Provide a JSON object string compatible with MapLibre GL Light specification.
     * Example: { "anchor": "viewport", "intensity": 0.5 }
     *
     * @param lightOptionsJson Raw JSON object string with light options.
     */
    fun setLight(lightOptionsJson: String) = stylableWorker.setLight(lightOptionsJson)

    /**
     * Sets the sky appearance (colors, blends, or expressions).
     * Any field not explicitly provided keeps its previous value.
     */
    fun setSky(sky: com.maptiler.maptilersdk.map.options.MTSky) = stylableWorker.setSky(sky)

    /**
     * Sets the globe space background (deep space/skybox).
     * Any field not explicitly provided keeps its previous value.
     */
    fun setSpace(space: com.maptiler.maptilersdk.map.options.MTSpace) = stylableWorker.setSpace(space)

    /**
     * Sets the atmospheric halo (glow) configuration on the globe.
     */
    fun setHalo(halo: com.maptiler.maptilersdk.map.options.MTHalo) = stylableWorker.setHalo(halo)

    /** Enables halo with default gradient. */
    fun enableHalo() = stylableWorker.enableHalo()

    /** Disables halo (animates out). */
    fun disableHalo() = stylableWorker.disableHalo()

    /** Disables halo state transitions (animations). */
    fun disableHaloAnimations() = stylableWorker.disableHaloAnimations()

    /** Disables space state transitions (animations). */
    fun disableSpaceAnimations() = stylableWorker.disableSpaceAnimations()

    /**
     * Sets whether world copies should render.
     *
     * @param shouldRenderWorldCopies If true, wrap the world horizontally.
     */
    fun setRenderWorldCopies(shouldRenderWorldCopies: Boolean) = stylableWorker.setRenderWorldCopies(shouldRenderWorldCopies)

    /**
     * Sets the current style by reference style and optional variant.
     * Changing the style resets sources and layers.
     *
     * @param reference Reference style.
     * @param variant Optional style variant.
     */
    fun setStyle(
        reference: MTMapReferenceStyle,
        variant: MTMapStyleVariant? = null,
    ) {
        this.referenceStyle = reference
        this.styleVariant = variant
        // Apply the new style on the JS side first.
        stylableWorker.setStyle(reference, variant)

        // After a style change, the underlying map resets sources and layers.
        // Clear local caches and any pending tasks to avoid stale state.
        mapSources.clear()
        mapLayers.clear()
        queue.clear()
    }

    // Property setters (sync)
    fun setFilter(
        layerId: String,
        filter: PropertyValue,
    ) = stylableWorker.setFilter(layerId, filter)

    fun setLayoutProperty(
        layerId: String,
        name: String,
        value: PropertyValue,
    ) = stylableWorker.setLayoutProperty(layerId, name, value)

    fun setPaintProperty(
        layerId: String,
        name: String,
        value: PropertyValue,
    ) = stylableWorker.setPaintProperty(layerId, name, value)

    // Property setters (suspend)
    suspend fun setFilterAwait(
        layerId: String,
        filter: PropertyValue,
    ) = stylableWorker.setFilterAwait(layerId, filter)

    suspend fun setLayoutPropertyAwait(
        layerId: String,
        name: String,
        value: PropertyValue,
    ) = stylableWorker.setLayoutPropertyAwait(layerId, name, value)

    suspend fun setPaintPropertyAwait(
        layerId: String,
        name: String,
        value: PropertyValue,
    ) = stylableWorker.setPaintPropertyAwait(layerId, name, value)
}

internal class StyleTask(
    val name: String,
    private val action: () -> Unit,
) {
    fun execute() = action()
}
