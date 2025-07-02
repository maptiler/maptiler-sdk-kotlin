/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map

import com.maptiler.maptilersdk.map.types.MTLanguage
import com.maptiler.maptilersdk.map.types.MTLanguageSerializer
import com.maptiler.maptilersdk.map.types.MTMapCorner
import com.maptiler.maptilersdk.map.types.MTProjectionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Parameters of the map object.
 */
@Serializable
class MTMapOptions {
    /**
     * The language of the map.
     */
    @Serializable(with = MTLanguageSerializer::class)
    var language: MTLanguage? = null
        private set

    /**
     * The geographical centerpoint of the map.
     *
     * If center is not specified, SDK will look for it in the map style object.
     * If it is not specified in the style, it will default to (latitude: 0.0, longitude: 0.0).
     */
    var center: LngLat? = null
        private set

    /**
     * Projection type of the map object.
     *
     * This will overwrite the projection property from the style (if any).
     */
    var projection: MTProjectionType? = null
        private set

    /**
     * The zoom level of the map.
     *
     * If zoom is not specified, SDK will look for it in the map style object.
     * If it is not specified in the style, it will default to 0.0.
     */
    var zoom: Double? = null
        private set

    /**
     * The maximum zoom level of the map (0-24).
     */
    var maxZoom: Double? = null
        private set

    /**
     * The minimum zoom level of the map (0-24).
     */
    var minZoom: Double? = null
        private set

    /**
     * The bearing of the map, measured in degrees counter-clockwise from north.
     *
     * If bearing is not specified, SDK will look for it in the map style object.
     * If it is not specified in the style, it will default to 0.0.
     */
    var bearing: Double? = null
        private set

    /**
     * The threshold, measured in degrees, that determines when the map's bearing will snap to north.
     */
    var bearingSnap: Double? = null
        private set

    /**
     * The pitch (tilt) of the map, measured in degrees away from the plane of the screen (0-85).
     *
     * If pitch is not specified, SDK will look for it in the map style object.
     * If it is not specified in the style, it will default to 0.0.
     */
    var pitch: Double? = null
        private set

    /**
     * The maximum pitch of the map (0-180).
     */
    var maxPitch: Double? = null
        private set

    /**
     * The minimum pitch of the map (0-85).
     */
    var minPitch: Double? = null
        private set

    /**
     * The roll angle of the map, measured in degrees counter-clockwise about the camera boresight.
     *
     * If roll is not specified, SDK will look for it in the map style object.
     * If it is not specified in the style, it will default to 0.0.
     */
    var roll: Double? = null
        private set

    /**
     * Boolean indicating whether the map's roll control with "drag to rotate" interaction is enabled.
     */
    @SerialName("rollEnabled")
    var rollIsEnabled: Boolean? = null
        private set

    /**
     * The elevation of the geographical centerpoint of the map, in meters above sea level.
     *
     * If elevation is not specified, SDK will look for it in the map style object.
     * If it is not specified in the style, it will default to 0.0.
     */
    var elevation: Double? = null
        private set

    /**
     * Boolean indicating whether 3D terrain is enabled.
     */
    @SerialName("terrain")
    var terrainIsEnabled: Boolean? = null
        private set

    /**
     * 3D terrain exaggeration factor.
     */
    var terrainExaggeration: Double? = null
        private set

    /**
     * Determines whether to cancel, or retain, tiles from the current viewport which are still loading
     * but which belong to a farther (smaller) zoom level than the current one.
     *
     * If true, when zooming in, tiles which didn't manage to load for previous zoom levels will become canceled.
     * This might save some computing resources for slower devices, but the map details might appear more
     * abruptly at the end of the zoom. If false, when zooming in, the previous zoom level(s) tiles will progressively
     * appear, giving a smoother map details experience. However, more tiles will be rendered
     * in a short period of time.
     */
    var cancelPendingTileRequestsWhileZooming: Boolean? = null
        private set

    /**
     * Boolean indicating whether center is clamped to the ground.
     *
     * If true, the elevation of the center point will automatically be set to the terrain elevation
     * (or zero if terrain is not enabled). If false, the elevation of the center point will default
     * to sea level and will not automatically update.
     * Needs to be set to false to keep the camera above ground when pitch > 90 degrees.
     */
    @SerialName("centerClampedToGround")
    var isCenterClampedToGround: Boolean? = null
        private set

    /**
     * Boolean indicating whether Resource Timing API information will be collected.
     */
    @SerialName("collectResourceTiming")
    var shouldCollectResourceTiming: Boolean? = null
        private set

    /**
     * Boolean indicating whether cross source collisions are enabled.
     *
     * If true, symbols from multiple sources can collide with each other during collision detection.
     * If false, collision detection is run separately for the symbols in each source.
     */
    @SerialName("crossSourceCollisions")
    var crossSourceCollisionsAreEnabled: Boolean? = null
        private set

    /**
     * The duration of the fade-in/fade-out animation for label collisions, in milliseconds.
     *
     * This setting affects all symbol layers. This setting does not affect the duration of runtime
     * styling transitions or raster tile cross-fading.
     */
    var fadeDuration: Double? = null
        private set

    /**
     * Boolean indicating whether interaction on the map is enabled.
     */
    @SerialName("interactive")
    var isInteractionEnabled: Boolean? = null
        private set

    /**
     * A value representing the position of the MapTiler wordmark on the map.
     */
    var logoPosition: MTMapCorner? = MTMapCorner.TOP_LEFT
        private set

    /**
     * Boolean indicating whether MapTiler logo is visible on the map.
     *
     * If true, the MapTiler logo will be shown. false will only work on premium accounts.
     */
    @SerialName("maptilerLogo")
    var maptilerLogoIsVisible: Boolean? = null
        private set

    /**
     * The maximum number of tiles stored in the tile cache for a given source.
     *
     * If omitted, the cache will be dynamically sized based on the current viewport.
     */
    var maxTileCacheSize: Double? = null
        private set

    /**
     * The maximum number of zoom levels for which to store tiles for a given source.
     *
     * Tile cache dynamic size is calculated by multiplying maxTileCacheZoomLevels
     * with the approximate number of tiles in the viewport for a given source.
     */
    var maxTileCacheZoomLevels: Double? = null
        private set

    /**
     * Boolean indicating whether the map's pitch control with drag to rotate interaction will be disabled.
     */
    @SerialName("pitchWithRotate")
    var shouldPitchWithRotate: Boolean? = null
        private set

    /**
     * Boolean indicating whether the map won't attempt to re-request tiles once they expire.
     */
    @SerialName("refreshExpiredTiles")
    var shouldRefreshExpiredTiles: Boolean? = null
        private set

    /**
     * Boolean indicating whether multiple copies of the world will be rendered side by side
     * beyond -180 and 180 degrees longitude.
     */
    @SerialName("renderWorldCopies")
    var shouldRenderWorldCopies: Boolean? = null
        private set

    /**
     * Boolean indicating whether the drag to pitch" interaction is enabled.
     */
    @SerialName("touchPitch")
    var shouldDragToPitch: Boolean? = null
        private set

    /**
     * Boolean indicating whether the pinch to rotate and zoom interaction is enabled.
     */
    @SerialName("touchZoomRotate")
    var shouldPinchToRotateAndZoom: Boolean? = null
        private set

    /**
     * Boolean indicating whether the double tap to zoom interaction is enabled.
     */
    @SerialName("doubleClickZoom")
    var doubleTapShouldZoom: Boolean? = null
        private set

    /**
     * Boolean indicating whether the drag to pan interaction is enabled.
     */
    @SerialName("dragPan")
    var dragPanIsEnabled: Boolean? = null
        private set

    /**
     * Boolean indicating whether the drag to rotate interaction is enabled.
     */
    @SerialName("dragRotate")
    var dragRotateIsEnabled: Boolean? = null
        private set

    /**
     * Boolean indicating whether style should be validated.
     */
    var shouldValidateStyle: Boolean? = null
        private set

    /**
     * Boolean indicating whether minimap control is added directly to the map.
     */
    @SerialName("minimap")
    var minimapIsVisible: Boolean? = false
        private set

    /**
     * Boolean indicating whether attribution control is added directly to the map.
     */
    @SerialName("attributionControl")
    var attributionControlIsVisible: Boolean? = null
        private set

    /**
     * Boolean indicating whether geolocate control is added directly to the map.
     */
    @SerialName("geolocateControl")
    var geolocateControlIsVisible: Boolean? = false
        private set

    /**
     * Boolean indicating whether navigation control is added directly to the map.
     */
    @SerialName("navigationControl")
    var navigationControlIsVisible: Boolean? = false
        private set

    /**
     * Boolean indicating whether projection control is added directly to the map.
     */
    @SerialName("projectionControl")
    var projectionControlIsVisible: Boolean? = false
        private set

    /**
     * Boolean indicating whether scale control is added directly to the map.
     */
    @SerialName("scaleControl")
    var scaleControlIsVisible: Boolean? = false
        private set

    /**
     * Boolean indicating whether terrain control is added directly to the map.
     */
    @SerialName("terrainControl")
    var terrainControlIsVisible: Boolean? = false
        private set

    /**
     * Boolean indicating whether session logic is enabled.
     *
     * This allows MapTiler to enable "session based billing".
     * Defaults to true.
     * @see <https://docs.maptiler.com/guides/maps-apis/maps-platform/what-is-map-session-in-maptiler-cloud/>
     */
    var isSessionLogicEnabled: Boolean = true
        private set

    /** Initializes map options with center, zoom, bearing, and pitch. */
    constructor(center: LngLat?, zoom: Double?, bearing: Double?, pitch: Double?) {
        this.center = center
        this.zoom = zoom
        this.bearing = bearing
        this.pitch = pitch
    }

    /** Initializes map options with center and zoom. */
    constructor(center: LngLat?, zoom: Double?) {
        this.center = center
        this.zoom = zoom
    }

    /** Initializes map options with center, zoom and language. */
    constructor(center: LngLat?, zoom: Double?, language: MTLanguage) {
        this.center = center
        this.zoom = zoom
        this.language = language
    }

    /** Initializes map options with center, zoom and terrain. */
    constructor(center: LngLat?, zoom: Double?, terrainIsEnabled: Boolean?, terrainExaggeration: Double?) {
        this.center = center
        this.zoom = zoom
        this.terrainIsEnabled = terrainIsEnabled
        this.terrainExaggeration = terrainExaggeration
    }

    /** Initializes map options with center, zoom and projection. */
    constructor(center: LngLat?, zoom: Double?, projection: MTProjectionType?) {
        this.center = center
        this.zoom = zoom
        this.projection = projection
    }

    /** Full constructor with optional parameters for all properties. */
    constructor() {
        // Defaults
    }

    // Setters

    fun setMapTilerLogoIsVisible(isVisible: Boolean) {
        maptilerLogoIsVisible = isVisible
    }
}
