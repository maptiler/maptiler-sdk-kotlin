/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
import com.maptiler.maptilersdk.helpers.MTConnectivity
import kotlinx.coroutines.Dispatchers
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
        val retryPolicy = MTNetworkRetryPolicy(maxAttempts = 3)

        try {
            retryPolicy.execute {
                // Ensure network is available before attempting download
                MTConnectivity.suspendUntilNetworkAvailable(context)

                // If the file exists and its size matches the expected size (if provided), skip it.
                val destFile = destinationFile
                if (destFile != null && MTOfflineStorage.isFileVerified(destFile)) {
                    val expectedSize = resource.size
                    if (expectedSize == null || destFile.length() == expectedSize) {
                        return@execute // Success, nothing to do
                    }
                }

                performDownload()
            }
        } catch (e: MTOfflineError) {
            throw e
        } catch (e: IOException) {
            throw MTOfflineError.NetworkError(e)
        } catch (e: Exception) {
            throw MTOfflineError.DownloadFailed(e)
        }
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
                    429 -> throw MTOfflineError.BadResponse(429)
                    in 500..599 -> throw MTOfflineError.BadResponse(statusCode)
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
