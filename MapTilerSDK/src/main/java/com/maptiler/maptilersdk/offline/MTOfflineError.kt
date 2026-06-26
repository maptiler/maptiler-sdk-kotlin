/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

/**
 * Provides additional context for an offline error.
 */
public data class MTOfflineContext(
    /**
     * The URL of the resource that failed to download.
     */
    val url: String,
    /**
     * An optional identifier for the resource (e.g., Tile ID, Source ID).
     */
    val resourceId: String? = null,
)

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
 * Represents errors that can occur during the offline planning and downloading process.
 */
public sealed class MTOfflineError : Exception() {
    // MARK: - HTTP Failures

    /**
     * The provided URL was invalid.
     */
    public data class InvalidURL(val url: String) : MTOfflineError() {
        override val message: String get() = "The provided URL is invalid: $url."
    }

    /**
     * The server returned a bad HTTP response with the given status code.
     */
    public data class BadResponse(val statusCode: Int) : MTOfflineError() {
        override val message: String get() = "The server returned a bad response with status code: $statusCode."
    }

    /**
     * A general network connectivity issue occurred.
     */
    public data class NetworkError(override val cause: Throwable) : MTOfflineError() {
        override val message: String? get() = "A network error occurred: ${cause.localizedMessage}."
    }

    /**
     * The server returned a 204 No Content response, which is unexpected for this resource.
     */
    public object NoContent : MTOfflineError() {
        override val message: String get() = "The server returned no content (204) for a required resource."
    }

    /**
     * The received content format or type does not match the expected format.
     */
    public data class ContentMismatch(val expected: String, val actual: String) : MTOfflineError() {
        override val message: String get() = "Content mismatch: expected $expected, but received $actual."
    }

    // MARK: - JSON Failures

    /**
     * The JSON data is malformed or invalid.
     */
    public object MalformedJSON : MTOfflineError() {
        override val message: String get() = "The JSON data is malformed or invalid."
    }

    /**
     * A required key is missing in style.json or TileJSON.
     */
    public data class MissingKey(val key: String) : MTOfflineError() {
        override val message: String get() = "A required key is missing in the JSON data: '$key'."
    }

    /**
     * An error occurred while decoding JSON.
     */
    public data class DecodingError(override val cause: Throwable) : MTOfflineError() {
        override val message: String? get() = "An error occurred while decoding JSON: ${cause.localizedMessage}."
    }

    // Domain-Specific Cases

    /**
     * The provided bounding box is invalid.
     */
    public object InvalidBoundingBox : MTOfflineError() {
        override val message: String get() = "The provided bounding box is invalid."
    }

    /**
     * The minimum zoom level is greater than the maximum zoom level.
     */
    public data class ReversedZoomLevels(val minZoom: Double, val maxZoom: Double) : MTOfflineError() {
        override val message: String get() = "The minimum zoom level ($minZoom) is greater than the maximum zoom level ($maxZoom)."
    }

    /**
     * The API key is missing.
     */
    public object MissingAPIKey : MTOfflineError() {
        override val message: String get() = "The MapTiler API key is missing. Please configure the SDK with a valid API key."
    }

    // MARK: - Storage and Limitations

    /**
     * The device does not have enough available storage space to complete the download.
     */
    public object InsufficientStorage : MTOfflineError() {
        override val message: String get() = "There is not enough storage space available on the device to complete the download."
    }

    /**
     * The requested offline region exceeds the maximum allowed tile count or size.
     */
    public data class ExceedsMaximumTileCount(val limit: Int, val requested: Int) : MTOfflineError() {
        override val message: String get() = "The download request of $requested tiles exceeds the maximum allowed limit of $limit tiles."
    }

    /**
     * A file system error occurred while attempting to save or read offline data.
     */
    public data class FileSystemError(val storageError: MTOfflineStorageError) : MTOfflineError() {
        override val message: String? get() = storageError.message
    }

    /**
     * The requested offline region resulted in no tiles.
     */
    public object InvalidRegion : MTOfflineError() {
        override val message: String get() = "The requested offline region resulted in no tiles."
    }

    /**
     * The offline download or operation was cancelled.
     */
    public object Cancelled : MTOfflineError() {
        override val message: String get() = "The offline operation was cancelled."
    }

    /**
     * The offline pack is invalid or corrupted.
     */
    public object InvalidPack : MTOfflineError() {
        override val message: String get() = "The offline pack is invalid or corrupted."
    }

    /**
     * A download operation failed.
     */
    public data class DownloadFailed(override val cause: Throwable) : MTOfflineError() {
        override val message: String? get() = cause.localizedMessage
    }
}
