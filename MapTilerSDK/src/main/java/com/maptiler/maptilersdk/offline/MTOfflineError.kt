/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

/**
 * Errors that can occur during offline storage operations.
 */
public sealed class MTOfflineStorageError : Exception() {
    /**
     * Failed to write to the file system.
     */
    public data class WriteFailed(override val cause: Throwable) : MTOfflineStorageError() {
        override val message: String?
            get() = "A file system error occurred: ${cause.localizedMessage}"
    }

    /**
     * Failed to read from the file system.
     */
    public data class ReadFailed(override val cause: Throwable) : MTOfflineStorageError() {
        override val message: String?
            get() = "A file system error occurred: ${cause.localizedMessage}"
    }
}

/**
 * Represents errors related to offline packs.
 */
public sealed class MTOfflineError : Exception() {
    /**
     * An error occurred within the underlying storage system.
     */
    public data class FileSystemError(val storageError: MTOfflineStorageError) : MTOfflineError()

    /**
     * The requested region definition is invalid.
     */
    public object InvalidRegion : MTOfflineError()

    /**
     * The operation was cancelled.
     */
    public object Cancelled : MTOfflineError()

    /**
     * The offline pack is invalid or corrupted.
     */
    public object InvalidPack : MTOfflineError()

    /**
     * A download operation failed.
     */
    public data class DownloadFailed(override val cause: Throwable) : MTOfflineError()

    /**
     * The requested region exceeds the maximum allowed resource count.
     */
    public data class ExceedsMaximumTileCount(val limit: Int, val requested: Int) : MTOfflineError() {
        override val message: String
            get() = "The requested region exceeds the maximum allowed resource count (Limit: $limit, Requested: $requested)"
    }

    /**
     * The server returned a bad response code.
     */
    public data class BadResponse(val statusCode: Int) : MTOfflineError()

    /**
     * A network error occurred.
     */
    public data class NetworkError(override val cause: Throwable) : MTOfflineError()

    /**
     * The downloaded content type does not match the expected type.
     */
    public data class ContentMismatch(val expected: String, val actual: String) : MTOfflineError()
}
