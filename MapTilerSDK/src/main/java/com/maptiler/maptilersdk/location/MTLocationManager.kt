/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger

/**
 * Represents MTLocationManager errors.
 */
sealed class MTLocationError(
    message: String,
) : Exception(message) {
    /** Location permission denied. */
    object PermissionDenied : MTLocationError("Location permission denied")
}

/**
 * Protocol requirements for location manager delegate.
 */
interface MTLocationManagerDelegate {
    /** Triggered when location updates. */
    @MainThread
    fun didUpdateLocation(location: Location)

    /** Triggered when location updates fail. */
    @MainThread
    fun didFailWithError(error: Throwable)
}

/**
 * Class responsible for location updates.
 *
 * This manager uses the platform [LocationManager] and calls delegate callbacks on the main thread.
 * Runtime permission requests must be handled by the app; use [hasLocationPermission] to check state.
 */
class MTLocationManager(
    private val context: Context,
    private val minTimeMs: Long = 1000L,
    private val minDistanceM: Float = 0f,
) {
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val mainHandler = Handler(Looper.getMainLooper())

    var delegate: MTLocationManagerDelegate? = null

    private val listener =
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                mainHandler.post { delegate?.didUpdateLocation(location) }
            }

            @Deprecated("Deprecated in Android 12")
            override fun onStatusChanged(
                provider: String?,
                status: Int,
                extras: Bundle?,
            ) {
                // No-op
            }

            override fun onProviderEnabled(provider: String) { /* No-op */ }

            override fun onProviderDisabled(provider: String) { /* No-op */ }
        }

    /** Starts the location updates. */
    fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            MTLogger.log("Location permission not granted.", MTLogType.WARNING)
            mainHandler.post { delegate?.didFailWithError(MTLocationError.PermissionDenied) }
            return
        }

        try {
            // Prefer GPS if fine permission is granted; also listen to network for quicker fixes.
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                requestUpdatesSafe(LocationManager.GPS_PROVIDER)
            }
            requestUpdatesSafe(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            MTLogger.log("Failed to start location updates: ${e.message}", MTLogType.ERROR)
            mainHandler.post { delegate?.didFailWithError(e) }
        }
    }

    /** Requests location only once and calls [MTLocationManagerDelegate.didUpdateLocation]. */
    fun requestLocationOnce() {
        if (!hasLocationPermission()) {
            MTLogger.log("Location permission not granted.", MTLogType.WARNING)
            mainHandler.post { delegate?.didFailWithError(MTLocationError.PermissionDenied) }
            return
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Prefer GPS if possible; fall back to network.
                val provider =
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        LocationManager.GPS_PROVIDER
                    } else {
                        LocationManager.NETWORK_PROVIDER
                    }
                val cancellation = CancellationSignal()
                locationManager.getCurrentLocation(
                    provider,
                    cancellation,
                    androidx.core.content.ContextCompat
                        .getMainExecutor(context),
                ) { location ->
                    if (location != null) {
                        mainHandler.post { delegate?.didUpdateLocation(location) }
                    } else {
                        mainHandler.post {
                            delegate?.didFailWithError(IllegalStateException("Location is null"))
                        }
                    }
                }
            } else {
                // API < 30: request single update via criteria
                val criteria =
                    Criteria().apply {
                        accuracy = Criteria.ACCURACY_FINE
                    }
                locationManager.requestSingleUpdate(
                    criteria,
                    object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            mainHandler.post { delegate?.didUpdateLocation(location) }
                        }

                        @Deprecated("Deprecated in Android 12")
                        override fun onStatusChanged(
                            provider: String?,
                            status: Int,
                            extras: Bundle?,
                        ) { /* No-op */ }

                        override fun onProviderEnabled(provider: String) { /* No-op */ }

                        override fun onProviderDisabled(provider: String) { /* No-op */ }
                    },
                    Looper.getMainLooper(),
                )
            }
        } catch (e: Exception) {
            MTLogger.log("Failed to get single location: ${e.message}", MTLogType.ERROR)
            mainHandler.post { delegate?.didFailWithError(e) }
        }
    }

    /** Stops the location updates. */
    fun stopLocationUpdates() {
        try {
            locationManager.removeUpdates(listener)
        } catch (e: Exception) {
            MTLogger.log("Failed to stop location updates: ${e.message}", MTLogType.WARNING)
        }
    }

    /** Returns true if coarse or fine location permission is granted. */
    fun hasLocationPermission(): Boolean {
        val coarse =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        val fine =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        return coarse || fine
    }

    private fun requestUpdatesSafe(provider: String) {
        if (locationManager.isProviderEnabled(provider)) {
            locationManager.requestLocationUpdates(provider, minTimeMs, minDistanceM, listener, Looper.getMainLooper())
        }
    }
}
