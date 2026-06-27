/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

/**
 * Manages background downloading using WorkManager.
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
        val workManager = WorkManager.getInstance(context)

        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

        val inputData =
            Data.Builder()
                .putString("packId", packId)
                .build()

        val workRequest =
            OneTimeWorkRequestBuilder<MTOfflineDownloadWorker>()
                .setConstraints(constraints)
                .setInputData(inputData)
                .addTag("offline_pack_$packId")
                .build()

        workManager.enqueueUniqueWork(
            "offline_pack_$packId",
            ExistingWorkPolicy.REPLACE,
            workRequest,
        )
    }

    /**
     * Cancels any pending or running background tasks for the given pack.
     */
    fun cancelTasks(
        context: Context,
        packId: String,
    ) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("offline_pack_$packId")
    }
}
