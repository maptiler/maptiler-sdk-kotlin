/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.maptiler.maptilersdk.map.MTMapOptions

/**
 * Simple device classifier and lean defaults applier.
 *
 * - Detects LOW/MID/HIGH tiers using RAM class, Android version, and some known models (e.g., Samsung A‑series).
 * - Applies performance‑lean defaults only where options are unset (null), so developer overrides win.
 */
internal object MTDeviceProfile {
    enum class Tier { LOW, MID, HIGH }

    fun detectTier(context: Context): Tier {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memClassMb = am?.memoryClass ?: 256
        val isLowRam =
            try {
                am?.isLowRamDevice ?: false
            } catch (_: Throwable) {
                false
            }

        val sdk = Build.VERSION.SDK_INT
        val manufacturer = (Build.MANUFACTURER ?: "").lowercase()
        val model = (Build.MODEL ?: "").uppercase()

        val isSamsungA = manufacturer == "samsung" && Regex("SM-A\\d+").containsMatchIn(model)

        // Conservative heuristics aimed at responsiveness, not perfect classification.
        if (isLowRam || memClassMb <= 128 || sdk < Build.VERSION_CODES.O || (isSamsungA && memClassMb <= 256)) {
            return Tier.LOW
        }

        if (memClassMb <= 256 || sdk < Build.VERSION_CODES.S || isSamsungA) {
            return Tier.MID
        }

        return Tier.HIGH
    }

    /**
     * Returns a new [MTMapOptions] that applies lean defaults only where fields are unset.
     * For LOW and MID devices we prefer lighter defaults; HIGH devices are returned unchanged.
     *
     * Pixel ratio defaults by tier (if unset by developer):
     * - LOW: 1.0
     * - MID: 1.5
     * - HIGH: unchanged (early return)
     */
    fun applyLeanDefaultsIfUnset(
        base: MTMapOptions,
        tier: Tier,
    ): MTMapOptions {
        if (tier == Tier.HIGH) return base

        val defaultPixelRatio =
            when (tier) {
                Tier.LOW -> 1.0
                Tier.MID -> 1.5
                Tier.HIGH -> base.pixelRatio ?: 1.0 // unreachable due to early return
            }

        // Merge: prefer base values where present; set only performance‑lean values when null.
        return MTMapOptions(
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
            cancelPendingTileRequestsWhileZooming = base.cancelPendingTileRequestsWhileZooming ?: true,
            isCenterClampedToGround = base.isCenterClampedToGround,
            shouldCollectResourceTiming = base.shouldCollectResourceTiming,
            crossSourceCollisionsAreEnabled = base.crossSourceCollisionsAreEnabled ?: false,
            fadeDuration = base.fadeDuration,
            isInteractionEnabled = base.isInteractionEnabled,
            logoPosition = base.logoPosition,
            maptilerLogoIsVisible = base.maptilerLogoIsVisible,
            maxTileCacheSize = base.maxTileCacheSize,
            maxTileCacheZoomLevels = base.maxTileCacheZoomLevels ?: 4.0,
            pixelRatio = base.pixelRatio ?: defaultPixelRatio,
            shouldPitchWithRotate = base.shouldPitchWithRotate,
            shouldRefreshExpiredTiles = base.shouldRefreshExpiredTiles ?: false,
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
            // Keep developer choice; default is CAMERA_ONLY (camera events without full touch/render)
            eventLevel = base.eventLevel,
            highFrequencyEventThrottleMs = base.highFrequencyEventThrottleMs ?: 150,
        )
    }

    /**
     * Convenience overload using [Context] to detect tier first.
     */
    fun applyLeanDefaultsIfUnset(
        context: Context,
        base: MTMapOptions,
    ): MTMapOptions = applyLeanDefaultsIfUnset(base, detectTier(context))
}
