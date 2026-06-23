/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import java.io.File

/**
 * A protocol representing an asset to be downloaded.
 */
internal interface MTDownloadTask {
    /**
     * The unique identifier for the task (usually the source URL).
     */
    val id: String

    /**
     * The destination file where the resource will be saved.
     */
    val destinationFile: File?

    /**
     * Executes the download task.
     */
    suspend fun execute()
}
