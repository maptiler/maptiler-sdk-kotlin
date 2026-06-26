/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.delay

internal object MTConnectivity {
    /**
     * Checks if the network is currently available.
     *
     * @param context Android context.
     * @return True if the device is connected to the internet, false otherwise.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Suspends until the network becomes available.
     *
     * @param context Android context.
     * @param checkIntervalMillis The interval at which to check for connectivity.
     */
    suspend fun suspendUntilNetworkAvailable(
        context: Context,
        checkIntervalMillis: Long = 2000L,
    ) {
        while (!isNetworkAvailable(context)) {
            delay(checkIntervalMillis)
        }
    }
}
