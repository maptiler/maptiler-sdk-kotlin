/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context

/**
 * Stub for Background Manager.
 * Will be implemented using WorkManager.
 */
internal object MTOfflineBackgroundManager {
    /**
     * Enqueues download tasks to be processed in the background.
     */
    fun enqueue(
        context: Context,
        packId: String,
        tasks: List<MTDownloadTask>,
    ) {
        // Implement using WorkManager
    }

    /**
     * Cancels any pending or running background tasks for the given pack.
     */
    fun cancelTasks(
        context: Context,
        packId: String,
    ) {
        // Implement using WorkManager
    }
}
