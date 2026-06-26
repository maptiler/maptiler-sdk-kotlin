/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.math.max

/**
 * Represents a downloadable offline region.
 */
public class MTOfflinePack internal constructor(
    /**
     * The unique identifier of the pack.
     */
    public val id: String,
    /**
     * The region definition of the pack.
     */
    public val region: MTOfflineRegionDefinition,
    private val context: Context,
    private val downloader: MTOfflineDownloader = MTOfflineDownloader(context),
) {
    /**
     * The metadata object linking state and region.
     */
    public var metadata: MTOfflinePackMetadata
        private set

    private val _stateFlow = MutableStateFlow(MTOfflinePackState.PENDING)

    /**
     * A Kotlin Flow emitting the current state of the pack download.
     */
    public val stateFlow: StateFlow<MTOfflinePackState> = _stateFlow.asStateFlow()

    private var internalProgress =
        MTOfflinePackProgress(
            totalResources = 0,
            downloadedResources = 0,
            totalTileResources = 0,
        )

    private val _progressFlow = MutableStateFlow(internalProgress)

    /**
     * A Kotlin Flow emitting the current progress of the pack download.
     */
    public val progressFlow: StateFlow<MTOfflinePackProgress> = _progressFlow.asStateFlow()

    private var lastProgressEventTime: Long = 0
    private var downloadStartTime: Long = 0
    private var initialDownloadedResources: Int = 0

    init {
        metadata =
            MTOfflinePackMetadata(
                id = id,
                state = MTOfflinePackState.PENDING,
                size = 0L,
                createdAt = Instant.now(),
                expiresAt = Instant.now().plusMillis(MTOfflineConfiguration.DEFAULT_EXPIRATION_INTERVAL),
                region = region,
                totalResources = 0,
                totalTileResources = 0,
                downloadedResources = 0,
            )
        _stateFlow.value = metadata.state
    }

    internal constructor(
        metadata: MTOfflinePackMetadata,
        context: Context,
        downloader: MTOfflineDownloader = MTOfflineDownloader(context),
    ) : this(metadata.id, metadata.region, context, downloader) {
        this.metadata = metadata

        internalProgress =
            MTOfflinePackProgress(
                totalResources = metadata.totalResources,
                downloadedResources = metadata.downloadedResources,
                totalTileResources = metadata.totalTileResources,
            )
        _progressFlow.value = internalProgress

        val nextState =
            if (metadata.isExpired) {
                MTOfflinePackState.EXPIRED
            } else if (metadata.state == MTOfflinePackState.DOWNLOADING) {
                MTOfflinePackState.PAUSED
            } else {
                metadata.state
            }

        changeState(nextState)
    }

    private fun changeState(newState: MTOfflinePackState) {
        if (_stateFlow.value != newState) {
            _stateFlow.value = newState
            metadata.state = newState
            CoroutineScope(Dispatchers.IO).launch { syncMetadataToDisk() }
        }
    }

    private suspend fun syncMetadataToDisk() {
        if (_stateFlow.value == MTOfflinePackState.CANCELED) return

        metadata.state = _stateFlow.value
        metadata.size = MTOfflineStorage.calculatePackSize(context, id)
        metadata.totalResources = internalProgress.totalResources
        metadata.downloadedResources = internalProgress.downloadedResources
        metadata.totalTileResources = internalProgress.totalTileResources

        try {
            MTOfflineStorage.saveMetadata(context, id, metadata)
        } catch (e: Exception) {
            // Ignore expected sync errors or log them
        }
    }

    internal suspend fun startDownload(manifest: MTManifest) {
        if (_stateFlow.value == MTOfflinePackState.DOWNLOADING) return

        changeState(MTOfflinePackState.DOWNLOADING)

        val tasks = buildTasks(manifest)

        internalProgress =
            internalProgress.copy(
                totalResources = tasks.size,
                totalTileResources = manifest.tiles.size,
                downloadedResources = 0,
                downloadSpeed = 0.0,
                estimatedTimeRemaining = null,
            )
        _progressFlow.value = internalProgress

        downloadStartTime = System.currentTimeMillis()
        lastProgressEventTime = downloadStartTime
        initialDownloadedResources = 0

        CoroutineScope(Dispatchers.IO).launch { syncMetadataToDisk() }

        try {
            downloader.download(tasks, id) { completed, skipped ->
                updateProgress(completed, skipped)
            }

            if (_stateFlow.value != MTOfflinePackState.PAUSED && _stateFlow.value != MTOfflinePackState.CANCELED) {
                changeState(MTOfflinePackState.COMPLETED)
                internalProgress =
                    internalProgress.copy(
                        downloadedResources = internalProgress.totalResources,
                        downloadSpeed = 0.0,
                        estimatedTimeRemaining = 0.0,
                    )
                _progressFlow.value = internalProgress
                syncMetadataToDisk()
            }
        } catch (e: Exception) {
            if (_stateFlow.value != MTOfflinePackState.PAUSED) {
                changeState(MTOfflinePackState.FAILED)
            }
            throw e
        }
    }

    private fun updateProgress(
        completed: Int,
        skipped: Int,
    ) {
        val newDownloaded = internalProgress.downloadedResources + completed + skipped

        val now = System.currentTimeMillis()
        val elapsedTotalSeconds = (now - downloadStartTime) / 1000.0

        var speed = 0.0
        var eta: Double? = null

        if (elapsedTotalSeconds > 0) {
            val downloadedSinceStart = max(0, newDownloaded - initialDownloadedResources)
            speed = downloadedSinceStart / elapsedTotalSeconds

            val remaining = internalProgress.totalResources - newDownloaded
            if (speed > 0) {
                eta = remaining / speed
            }
        }

        internalProgress =
            internalProgress.copy(
                downloadedResources = newDownloaded,
                downloadSpeed = speed,
                estimatedTimeRemaining = eta,
            )

        // Throttle progress events to at most 10 per second (~100ms)
        // Ensure we always emit 100% completion
        val shouldYield =
            (now - lastProgressEventTime) >= 100 ||
                internalProgress.downloadedResources == internalProgress.totalResources

        if (shouldYield) {
            lastProgressEventTime = now
            _progressFlow.value = internalProgress

            CoroutineScope(Dispatchers.IO).launch { syncMetadataToDisk() }
        }
    }

    private fun buildTasks(from: MTManifest): List<MTDownloadTask> {
        val tasks = mutableListOf<MTDownloadTask>()

        from.style?.let { style ->
            tasks.add(MTStyleDownloadTask(context, style, id))
        }

        val otherResources = from.tiles + from.glyphs + from.sprites
        tasks.addAll(otherResources.map { MTResourceDownloadTask(context, it, id) })

        return tasks
    }

    /**
     * Cancels the ongoing download.
     */
    public fun cancel() {
        if (_stateFlow.value == MTOfflinePackState.DOWNLOADING) {
            changeState(MTOfflinePackState.CANCELED)
            downloader.cancel()
        }
    }

    /**
     * Pauses the ongoing download.
     */
    public fun pause() {
        if (_stateFlow.value == MTOfflinePackState.DOWNLOADING) {
            changeState(MTOfflinePackState.PAUSED)
            downloader.cancel()
        }
    }
}
