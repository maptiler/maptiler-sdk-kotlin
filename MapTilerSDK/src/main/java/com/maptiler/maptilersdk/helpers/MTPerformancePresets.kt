/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.options.MTEventLevel

/**
 * Performance-oriented presets for MapTiler Kotlin SDK.
 */
object MTPerformancePresets {
    /**
     * Lean performance preset: prioritize responsiveness and bytes over fidelity.
     *
     * Applied overrides (favor performance over fidelity):
     * - pixelRatio = 1.0
     * - shouldRefreshExpiredTiles = false
     * - cancelPendingTileRequestsWhileZooming = true
     * - maxTileCacheZoomLevels = 4.0 (if unset)
     * - crossSourceCollisionsAreEnabled = false
     * - eventLevel = ESSENTIAL (keeps low-frequency events; per-frame events opt-in)
     * - highFrequencyEventThrottleMs = 150 (when ALL is enabled)
     */
    fun leanPerformance(base: MTMapOptions = MTMapOptions()): MTMapOptions =
        MTMapOptions(
            language = base.language,
            center = base.center,
            bounds = base.bounds,
            projection = base.projection,
            zoom = base.zoom,
            maxZoom = base.maxZoom,
            maxBounds = base.maxBounds,
            minZoom = base.minZoom,
            bearing = base.bearing,
            bearingSnap = base.bearingSnap,
            pitch = base.pitch,
            maxPitch = base.maxPitch,
            minPitch = base.minPitch,
            roll = base.roll,
            rollIsEnabled = base.rollIsEnabled,
            elevation = base.elevation,
            terrainIsEnabled = base.terrainIsEnabled,
            terrainExaggeration = base.terrainExaggeration,
            cancelPendingTileRequestsWhileZooming = true,
            isCenterClampedToGround = base.isCenterClampedToGround,
            shouldCollectResourceTiming = base.shouldCollectResourceTiming,
            crossSourceCollisionsAreEnabled = false,
            fadeDuration = base.fadeDuration,
            isInteractionEnabled = base.isInteractionEnabled,
            logoPosition = base.logoPosition,
            maptilerLogoIsVisible = base.maptilerLogoIsVisible,
            maxTileCacheSize = base.maxTileCacheSize,
            maxTileCacheZoomLevels = base.maxTileCacheZoomLevels ?: 4.0,
            pixelRatio = 1.0,
            shouldPitchWithRotate = base.shouldPitchWithRotate,
            shouldRefreshExpiredTiles = false,
            shouldRenderWorldCopies = base.shouldRenderWorldCopies,
            shouldDragToPitch = base.shouldDragToPitch,
            shouldPinchToRotateAndZoom = base.shouldPinchToRotateAndZoom,
            doubleTapShouldZoom = base.doubleTapShouldZoom,
            dragPanIsEnabled = base.dragPanIsEnabled,
            dragRotateIsEnabled = base.dragRotateIsEnabled,
            shouldValidateStyle = base.shouldValidateStyle,
            minimapIsVisible = base.minimapIsVisible,
            attributionControlIsVisible = base.attributionControlIsVisible,
            geolocateControlIsVisible = base.geolocateControlIsVisible,
            navigationControlIsVisible = base.navigationControlIsVisible,
            projectionControlIsVisible = base.projectionControlIsVisible,
            scaleControlIsVisible = base.scaleControlIsVisible,
            terrainControlIsVisible = base.terrainControlIsVisible,
            space = base.space,
            halo = base.halo,
            isSessionLogicEnabled = base.isSessionLogicEnabled,
            eventLevel = MTEventLevel.ESSENTIAL,
            highFrequencyEventThrottleMs = base.highFrequencyEventThrottleMs ?: 150,
        )

    /**
     * Returns a new [MTMapOptions] based on [base] with balanced performance defaults.
     *
     * Keeps crisper rendering by using a higher pixel ratio.
     *
     * Applied overrides:
     * - pixelRatio = base.pixelRatio ?: 1.5 (sharper than 1.0)
     * - shouldRefreshExpiredTiles = false
     * - cancelPendingTileRequestsWhileZooming = true
     * - maxTileCacheZoomLevels = 4.0 (if unset)
     * - crossSourceCollisionsAreEnabled = false
     * - eventLevel = ESSENTIAL (per-frame events opt-in)
     * - highFrequencyEventThrottleMs = 150 (when ALL is enabled)
     */
    fun balancedPerformance(base: MTMapOptions = MTMapOptions()): MTMapOptions =
        MTMapOptions(
            language = base.language,
            center = base.center,
            bounds = base.bounds,
            projection = base.projection,
            zoom = base.zoom,
            maxZoom = base.maxZoom,
            maxBounds = base.maxBounds,
            minZoom = base.minZoom,
            bearing = base.bearing,
            bearingSnap = base.bearingSnap,
            pitch = base.pitch,
            maxPitch = base.maxPitch,
            minPitch = base.minPitch,
            roll = base.roll,
            rollIsEnabled = base.rollIsEnabled,
            elevation = base.elevation,
            terrainIsEnabled = base.terrainIsEnabled,
            terrainExaggeration = base.terrainExaggeration,
            cancelPendingTileRequestsWhileZooming = true,
            isCenterClampedToGround = base.isCenterClampedToGround,
            shouldCollectResourceTiming = base.shouldCollectResourceTiming,
            crossSourceCollisionsAreEnabled = false,
            fadeDuration = base.fadeDuration,
            isInteractionEnabled = base.isInteractionEnabled,
            logoPosition = base.logoPosition,
            maptilerLogoIsVisible = base.maptilerLogoIsVisible,
            maxTileCacheSize = base.maxTileCacheSize,
            maxTileCacheZoomLevels = base.maxTileCacheZoomLevels ?: 4.0,
            pixelRatio = base.pixelRatio ?: 1.5,
            shouldPitchWithRotate = base.shouldPitchWithRotate,
            shouldRefreshExpiredTiles = false,
            shouldRenderWorldCopies = base.shouldRenderWorldCopies,
            shouldDragToPitch = base.shouldDragToPitch,
            shouldPinchToRotateAndZoom = base.shouldPinchToRotateAndZoom,
            doubleTapShouldZoom = base.doubleTapShouldZoom,
            dragPanIsEnabled = base.dragPanIsEnabled,
            dragRotateIsEnabled = base.dragRotateIsEnabled,
            shouldValidateStyle = base.shouldValidateStyle,
            minimapIsVisible = base.minimapIsVisible,
            attributionControlIsVisible = base.attributionControlIsVisible,
            geolocateControlIsVisible = base.geolocateControlIsVisible,
            navigationControlIsVisible = base.navigationControlIsVisible,
            projectionControlIsVisible = base.projectionControlIsVisible,
            scaleControlIsVisible = base.scaleControlIsVisible,
            terrainControlIsVisible = base.terrainControlIsVisible,
            space = base.space,
            halo = base.halo,
            isSessionLogicEnabled = base.isSessionLogicEnabled,
            eventLevel = MTEventLevel.ESSENTIAL,
            highFrequencyEventThrottleMs = base.highFrequencyEventThrottleMs ?: 150,
        )

    /**
     * Returns a new [MTMapOptions] based on [base] tuned for higher-end devices.
     *
     * Focuses on visual fidelity while keeping sensible performance guardrails.
     *
     * Applied overrides:
     * - pixelRatio = base.pixelRatio ?: 2.0 (very crisp; test for memory on low-end)
     * - shouldRefreshExpiredTiles = true (prefer up-to-date tiles)
     * - cancelPendingTileRequestsWhileZooming = false (allow progressive detail during zoom)
     * - maxTileCacheZoomLevels = 6.0 (if unset) to reduce churn when navigating
     * - crossSourceCollisionsAreEnabled = base.crossSourceCollisionsAreEnabled ?: true
     * - eventLevel = ESSENTIAL (per-frame events remain opt-in)
     * - highFrequencyEventThrottleMs = 100 (slightly more responsive when ALL is enabled)
     */
    fun highFidelity(base: MTMapOptions = MTMapOptions()): MTMapOptions =
        MTMapOptions(
            language = base.language,
            center = base.center,
            bounds = base.bounds,
            projection = base.projection,
            zoom = base.zoom,
            maxZoom = base.maxZoom,
            maxBounds = base.maxBounds,
            minZoom = base.minZoom,
            bearing = base.bearing,
            bearingSnap = base.bearingSnap,
            pitch = base.pitch,
            maxPitch = base.maxPitch,
            minPitch = base.minPitch,
            roll = base.roll,
            rollIsEnabled = base.rollIsEnabled,
            elevation = base.elevation,
            terrainIsEnabled = base.terrainIsEnabled,
            terrainExaggeration = base.terrainExaggeration,
            cancelPendingTileRequestsWhileZooming = false,
            isCenterClampedToGround = base.isCenterClampedToGround,
            shouldCollectResourceTiming = base.shouldCollectResourceTiming,
            crossSourceCollisionsAreEnabled = base.crossSourceCollisionsAreEnabled ?: true,
            fadeDuration = base.fadeDuration,
            isInteractionEnabled = base.isInteractionEnabled,
            logoPosition = base.logoPosition,
            maptilerLogoIsVisible = base.maptilerLogoIsVisible,
            maxTileCacheSize = base.maxTileCacheSize,
            maxTileCacheZoomLevels = base.maxTileCacheZoomLevels ?: 6.0,
            pixelRatio = base.pixelRatio ?: 2.0,
            shouldPitchWithRotate = base.shouldPitchWithRotate,
            shouldRefreshExpiredTiles = true,
            shouldRenderWorldCopies = base.shouldRenderWorldCopies,
            shouldDragToPitch = base.shouldDragToPitch,
            shouldPinchToRotateAndZoom = base.shouldPinchToRotateAndZoom,
            doubleTapShouldZoom = base.doubleTapShouldZoom,
            dragPanIsEnabled = base.dragPanIsEnabled,
            dragRotateIsEnabled = base.dragRotateIsEnabled,
            shouldValidateStyle = base.shouldValidateStyle,
            minimapIsVisible = base.minimapIsVisible,
            attributionControlIsVisible = base.attributionControlIsVisible,
            geolocateControlIsVisible = base.geolocateControlIsVisible,
            navigationControlIsVisible = base.navigationControlIsVisible,
            projectionControlIsVisible = base.projectionControlIsVisible,
            scaleControlIsVisible = base.scaleControlIsVisible,
            terrainControlIsVisible = base.terrainControlIsVisible,
            space = base.space,
            halo = base.halo,
            isSessionLogicEnabled = base.isSessionLogicEnabled,
            eventLevel = MTEventLevel.ESSENTIAL,
            highFrequencyEventThrottleMs = base.highFrequencyEventThrottleMs ?: 100,
        )
}

/**
 * Applies the lean performance preset over this instance, returning a new options object.
 */
fun MTMapOptions.withLeanPerformanceDefaults(): MTMapOptions = MTPerformancePresets.leanPerformance(this)

/**
 * Returns a new [MTMapOptions] with balanced performance defaults applied over this instance.
 */
fun MTMapOptions.withBalancedPerformanceDefaults(): MTMapOptions = MTPerformancePresets.balancedPerformance(this)

/**
 * Returns a new [MTMapOptions] with high-fidelity defaults applied over this instance.
 */
fun MTMapOptions.withHighFidelityDefaults(): MTMapOptions = MTPerformancePresets.highFidelity(this)
