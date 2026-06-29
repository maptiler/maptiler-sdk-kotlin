/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * An actor-like class responsible for managing offline downloads.
 */
internal class MTOfflineDownloader(
    private val context: Context,
    private val maxInFlight: Int = 5,
) {
    @Volatile
    private var isPackCancelled: Boolean = false

    private val progressMutex = Mutex()

    /**
     * Downloads the specified tasks.
     *
     * @param tasks The list of tasks to execute.
     * @param packId The ID of the offline pack.
     * @param progressHandler A callback for progress updates (completed, skipped).
     */
    suspend fun download(
        tasks: List<MTDownloadTask>,
        packId: String,
        progressHandler: (suspend (completed: Int, skipped: Int) -> Unit)? = null,
    ) = withContext(Dispatchers.IO) {
        isPackCancelled = false

        MTOfflineStorage.cleanStaleTempFiles(context, packId)

        val (pendingTasks, skippedCount) = filterPendingTasks(tasks)
        progressMutex.withLock {
            progressHandler?.invoke(0, skippedCount)
        }

        if (pendingTasks.isEmpty()) return@withContext

        // Producer-consumer using Channel
        val channel = Channel<MTDownloadTask>(capacity = Channel.BUFFERED)

        try {
            coroutineScope {
                // Start workers (consumers)
                val workers =
                    List(maxInFlight) {
                        launch {
                            for (task in channel) {
                                if (isPackCancelled) break
                                try {
                                    task.execute()
                                    if (!isPackCancelled) {
                                        progressMutex.withLock {
                                            progressHandler?.invoke(1, 0)
                                        }
                                    }
                                } catch (e: Exception) {
                                    if (e !is CancellationException) {
                                        // Propagate the error up to fail the coroutineScope
                                        throw e
                                    }
                                }
                            }
                        }
                    }

                // Producer
                launch {
                    for (task in pendingTasks) {
                        if (isPackCancelled) break
                        channel.send(task)
                    }
                    channel.close() // Signal workers that no more tasks are coming
                }
            }
        } catch (e: Exception) {
            if (isPackCancelled || e is CancellationException) {
                throw CancellationException("Download cancelled")
            }
            throw e
        }
    }

    private fun filterPendingTasks(tasks: List<MTDownloadTask>): Pair<List<MTDownloadTask>, Int> {
        val pendingTasks = mutableListOf<MTDownloadTask>()
        var skippedCount = 0

        for (task in tasks) {
            val destFile = task.destinationFile
            if (destFile != null && MTOfflineStorage.isFileVerified(destFile)) {
                // Check for size mismatch if it's a resource task
                if (task is MTResourceDownloadTask) {
                    val expectedSize = task.resource.size
                    if (expectedSize == null || destFile.length() == expectedSize) {
                        skippedCount++
                        continue
                    }
                } else {
                    skippedCount++
                    continue
                }
            }
            pendingTasks.add(task)
        }

        return Pair(pendingTasks, skippedCount)
    }

    /**
     * Cancels the ongoing download process.
     */
    fun cancel() {
        isPackCancelled = true
    }
}
