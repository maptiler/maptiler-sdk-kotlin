/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A WorkManager worker responsible for downloading offline packs in the background.
 */
public class MTOfflineDownloadWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            val packId = inputData.getString("packId") ?: return@withContext Result.failure()

            MTLogger.log("Background download started for pack $packId", MTLogType.INFO)

            val metadata = MTOfflineStorage.loadMetadata(applicationContext, packId) ?: return@withContext Result.failure()
            val manifest = MTOfflineStorage.loadManifest(applicationContext, packId) ?: return@withContext Result.failure()

            val downloader = MTOfflineDownloader(applicationContext)
            val tasks = buildTasks(manifest, packId)

            metadata.state = MTOfflinePackState.DOWNLOADING
            MTOfflineStorage.saveMetadata(applicationContext, packId, metadata)

            var totalCompletedInSession = 0
            var initialSkipped = 0
            var lastSaveTime = 0L

            try {
                downloader.download(tasks, packId) { completed, skipped ->
                    if (skipped > 0 && totalCompletedInSession == 0) {
                        initialSkipped = skipped
                    }
                    totalCompletedInSession += completed

                    val currentDownloaded = initialSkipped + totalCompletedInSession
                    metadata.downloadedResources = currentDownloaded

                    val now = System.currentTimeMillis()
                    // Throttle metadata saves to at most once per second during download
                    if (now - lastSaveTime > 1000 || currentDownloaded == metadata.totalResources) {
                        lastSaveTime = now
                        metadata.size = MTOfflineStorage.calculatePackSize(applicationContext, packId)
                        MTOfflineStorage.saveMetadata(applicationContext, packId, metadata)
                    }
                }

                metadata.state = MTOfflinePackState.COMPLETED
                metadata.downloadedResources = metadata.totalResources
                metadata.size = MTOfflineStorage.calculatePackSize(applicationContext, packId)
                MTOfflineStorage.saveMetadata(applicationContext, packId, metadata)

                MTLogger.log("Background download completed for pack $packId", MTLogType.INFO)
                Result.success()
            } catch (e: Exception) {
                MTLogger.log("Background download failed for pack $packId: ${e.message}", MTLogType.ERROR)

                // If it was cancelled, we don't mark it as failed, it might be paused or cancelled by user
                if (isStopped) {
                    return@withContext Result.success()
                }

                metadata.state = MTOfflinePackState.FAILED
                MTOfflineStorage.saveMetadata(applicationContext, packId, metadata)
                Result.retry()
            }
        }

    private fun buildTasks(
        from: MTManifest,
        packId: String,
    ): List<MTDownloadTask> {
        val tasks = mutableListOf<MTDownloadTask>()
        val context = applicationContext

        from.style?.let { style ->
            tasks.add(MTStyleDownloadTask(context, style, packId))
        }

        val otherResources = from.tiles + from.glyphs + from.sprites
        tasks.addAll(otherResources.map { MTResourceDownloadTask(context, it, packId) })

        return tasks
    }
}
