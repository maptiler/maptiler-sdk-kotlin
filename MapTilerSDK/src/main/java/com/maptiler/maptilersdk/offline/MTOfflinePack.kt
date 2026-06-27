/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
import com.maptiler.maptilersdk.logging.MTLogType
import com.maptiler.maptilersdk.logging.MTLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID
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
    customMetadata: ByteArray? = null,
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

    /**
     * The current state of the pack download.
     */
    public val state: MTOfflinePackState get() = _stateFlow.value

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

    /**
     * The current progress of the pack download.
     */
    public val progress: MTOfflinePackProgress get() = _progressFlow.value

    /**
     * The date when the pack expires.
     */
    public val expiresAt: Instant get() = metadata.expiresAt

    /**
     * Returns true if the pack has passed its expiration date.
     */
    public val isExpired: Boolean get() = metadata.isExpired

    /**
     * Optional custom data, typically used to store application-specific context (e.g. JSON data).
     */
    public val contextData: ByteArray? get() = metadata.context

    private var isUsingBackground: Boolean = false
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
                context = customMetadata,
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
    ) : this(
        id = metadata.id,
        region = metadata.region,
        context = context,
        customMetadata = metadata.context,
        downloader = downloader,
    ) {
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
        synchronized(this) {
            if (_stateFlow.value != newState) {
                _stateFlow.value = newState
                metadata.state = newState
                CoroutineScope(Dispatchers.IO).launch { syncMetadataToDisk() }
            }
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
            MTLogger.log("Failed to sync MTOfflinePack metadata to disk for $id: ${e.message}", MTLogType.ERROR)
        }
    }

    /**
     * Starts the download process for this pack.
     * This generates the necessary manifest based on the pack's region definition
     * and then begins downloading all required resources.
     *
     * @param useBackground If true, the download will be enqueued in a background service (via WorkManager).
     */
    public suspend fun download(useBackground: Boolean = false) {
        val planner = MTOfflinePlannerFactory.createPlanner()
        val manifest = planner.generateManifest(region)
        startDownload(manifest, useBackground)
    }

    /**
     * Resumes a previously paused or failed download.
     *
     * @param useBackground If true, the download will resume in a background service (via WorkManager).
     */
    public suspend fun resume(useBackground: Boolean = false) {
        val manifest =
            MTOfflineStorage.loadManifest(context, id)
                ?: throw IllegalStateException("Manifest not found for pack $id")
        startDownload(manifest, useBackground)
    }

    /**
     * Refreshes an expired pack, validating or updating its resources and resetting its expiration limit.
     *
     * @param useBackground If true, the download will be enqueued in a background service (via WorkManager).
     */
    public suspend fun refresh(useBackground: Boolean = false) {
        // Reset the expiration date
        metadata.expiresAt = Instant.now().plusMillis(MTOfflineConfiguration.DEFAULT_EXPIRATION_INTERVAL)
        syncMetadataToDisk()

        val planner = MTOfflinePlannerFactory.createPlanner()
        val manifest = planner.generateManifest(region)
        startDownload(manifest, useBackground)
    }

    internal suspend fun startDownload(
        manifest: MTManifest,
        useBackground: Boolean = false,
    ) {
        synchronized(this) {
            if (_stateFlow.value == MTOfflinePackState.DOWNLOADING) return
            isUsingBackground = useBackground
            changeState(MTOfflinePackState.DOWNLOADING)
        }

        MTOfflineStorage.saveManifest(context, id, manifest)

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

        syncMetadataToDisk()

        if (useBackground) {
            MTOfflineBackgroundManager.enqueue(context, id, tasks)
            // The background manager takes over downloading and reporting.
        } else {
            try {
                downloader.download(tasks, id) { completed, skipped ->
                    updateProgress(completed, skipped)
                }

                synchronized(this) {
                    if (_stateFlow.value != MTOfflinePackState.PAUSED && _stateFlow.value != MTOfflinePackState.CANCELED) {
                        changeState(MTOfflinePackState.COMPLETED)
                        internalProgress =
                            internalProgress.copy(
                                downloadedResources = internalProgress.totalResources,
                                downloadSpeed = 0.0,
                                estimatedTimeRemaining = 0.0,
                            )
                        _progressFlow.value = internalProgress
                    }
                }
                syncMetadataToDisk()
            } catch (e: Exception) {
                synchronized(this) {
                    if (_stateFlow.value != MTOfflinePackState.PAUSED) {
                        changeState(MTOfflinePackState.FAILED)
                    }
                }
                throw e
            }
        }
    }

    private fun updateProgress(
        completed: Int,
        skipped: Int,
    ) {
        if (initialDownloadedResources == 0 && skipped > 0 && internalProgress.downloadedResources == 0) {
            initialDownloadedResources = skipped
        }

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
        synchronized(this) {
            if (_stateFlow.value == MTOfflinePackState.DOWNLOADING) {
                changeState(MTOfflinePackState.CANCELED)
                if (isUsingBackground) {
                    MTOfflineBackgroundManager.cancelTasks(context, id)
                } else {
                    downloader.cancel()
                }
            }
        }
    }

    /**
     * Pauses the ongoing download.
     */
    public fun pause() {
        synchronized(this) {
            if (_stateFlow.value == MTOfflinePackState.DOWNLOADING) {
                changeState(MTOfflinePackState.PAUSED)
                if (isUsingBackground) {
                    MTOfflineBackgroundManager.cancelTasks(context, id)
                } else {
                    downloader.cancel()
                }
            }
        }
    }

    /**
     * Deletes the offline pack.
     * This stops any ongoing downloads and removes all associated files and metadata from disk.
     */
    public suspend fun remove() {
        cancel()
        changeState(MTOfflinePackState.CANCELED)
        MTOfflineStorage.deletePack(context, id)
    }

    public companion object {
        /**
         * Creates a new offline pack and initializes its on-disk storage and metadata.
         *
         * @param context Android context.
         * @param region The definition of the region to be downloaded.
         * @param metadataContext Optional custom data (e.g. JSON) to attach to the pack metadata.
         * @return A newly initialized [MTOfflinePack].
         */
        public suspend fun createPack(
            context: Context,
            region: MTOfflineRegionDefinition,
            metadataContext: ByteArray? = null,
        ): MTOfflinePack =
            withContext(Dispatchers.IO) {
                val packId = UUID.randomUUID().toString()

                val pack =
                    MTOfflinePack(
                        id = packId,
                        region = region,
                        context = context,
                    )

                // Override initial metadata if context is provided
                if (metadataContext != null) {
                    pack.metadata = pack.metadata.copy(context = metadataContext)
                }

                // Ensure directory structure is created
                MTOfflineStoragePaths.getPackDirectory(context, packId).mkdirs()
                MTOfflineStoragePaths.getTilesDirectory(context, packId).mkdirs()
                MTOfflineStoragePaths.getGlyphsDirectory(context, packId).mkdirs()

                // Save initial metadata to disk
                MTOfflineStorage.saveMetadata(context, packId, pack.metadata)

                pack
            }

        /**
         * Estimates the pack size for a given region definition.
         *
         * @param region The definition of the region to estimate.
         * @return An [MTPackStats] object containing the estimates.
         */
        public suspend fun estimateSize(region: MTOfflineRegionDefinition): MTPackStats {
            val estimator = MTOfflineEstimator()
            return estimator.estimatePack(region)
        }

        /**
         * Retrieves all offline packs currently stored on disk.
         * The packs are stably sorted by their creation date (oldest first).
         *
         * @param context Android context.
         * @return A list of [MTOfflinePack] instances.
         */
        public suspend fun packs(context: Context): List<MTOfflinePack> {
            val metadataList = MTOfflineStorage.listMetadata(context)
            val sortedMetadata =
                metadataList.sortedWith(
                    compareBy<MTOfflinePackMetadata> { it.createdAt }
                        .thenBy { it.id },
                )
            return sortedMetadata.map { MTOfflinePack(it, context) }
        }

        /**
         * Cleans up packs that have been expired for longer than the grace period.
         * This permanently deletes the packs and their files from the disk.
         *
         * @param context Android context.
         */
        public suspend fun cleanupExpiredPacks(context: Context) {
            val allPacks = packs(context)
            val gracePeriodMillis = MTOfflineConfiguration.DEFAULT_GRACE_PERIOD
            val now = Instant.now()

            for (pack in allPacks) {
                if (pack.state == MTOfflinePackState.EXPIRED) {
                    val expiresAt = pack.expiresAt
                    if (now.isAfter(expiresAt.plusMillis(gracePeriodMillis))) {
                        pack.remove()
                    }
                }
            }
        }

        /**
         * Deletes all offline packs currently stored on disk.
         *
         * @param context Android context.
         */
        public suspend fun removeAll(context: Context) {
            val allPacks = packs(context)
            for (pack in allPacks) {
                pack.cancel()
            }

            withContext(Dispatchers.IO) {
                val rootDir = MTOfflineStoragePaths.getRootDirectory(context)
                if (rootDir.exists()) {
                    rootDir.deleteRecursively()
                }
            }
        }
    }
}
