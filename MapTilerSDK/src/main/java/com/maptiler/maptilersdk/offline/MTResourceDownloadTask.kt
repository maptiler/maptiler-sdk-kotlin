/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File
import java.io.IOException

/**
 * A concrete download task that fetches a single map resource.
 */
internal class MTResourceDownloadTask(
    private val context: Context,
    val resource: MTMapResource,
    val packId: String,
) : MTDownloadTask {
    override val id: String = resource.url

    override val destinationFile: File?
        get() = MTOfflineStoragePaths.getAbsoluteFile(context, packId, resource.destinationPath)

    override suspend fun execute() {
        val maxAttempts = 3
        var currentAttempt = 0
        var lastError: Exception? = null

        while (currentAttempt < maxAttempts) {
            try {
                performDownload()
                return // Success
            } catch (e: Exception) {
                currentAttempt++
                lastError = e
                if (currentAttempt < maxAttempts) {
                    delay(1000L * currentAttempt)
                }
            }
        }

        throw lastError ?: MTOfflineError.DownloadFailed(Exception("Unknown error"))
    }

    private suspend fun performDownload() =
        withContext(Dispatchers.IO) {
            val normalizedUrl = MTURLNormalizer.normalize(resource.url)

            val request =
                Request.Builder()
                    .url(normalizedUrl)
                    .build()

            MTOfflineHttpClient.client.newCall(request).execute().use { response ->
                val statusCode = response.code
                when (statusCode) {
                    204 -> return@withContext
                    in 200..299 -> {
                        val contentType = response.header("Content-Type")
                        validateContentType(contentType, resource.url)

                        val body = response.body ?: throw MTOfflineError.DownloadFailed(IOException("Empty response body"))
                        val data = body.bytes()

                        val destFile = destinationFile ?: return@withContext
                        MTOfflineStorage.write(data, destFile)
                    }
                    else -> throw MTOfflineError.BadResponse(statusCode)
                }
            }
        }

    private fun validateContentType(
        contentType: String?,
        resourceURL: String,
    ) {
        if (contentType == null) return

        val urlPath = resourceURL.lowercase()
        if (urlPath.endsWith(".pbf") || urlPath.endsWith(".mvt")) {
            if (contentType.contains("text/html")) {
                throw MTOfflineError.ContentMismatch(
                    expected = "application/x-protobuf",
                    actual = contentType,
                )
            }
        } else if (urlPath.endsWith(".png") || urlPath.endsWith(".jpg") || urlPath.endsWith(".jpeg") || urlPath.endsWith(".webp")) {
            if (contentType.contains("text/html") || contentType.contains("application/json")) {
                throw MTOfflineError.ContentMismatch(
                    expected = "image/*",
                    actual = contentType,
                )
            }
        }
    }
}
