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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

    private val rfc1123Formatter =
        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("GMT")
        }

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

            val destFile = destinationFile ?: return@withContext
            val requestBuilder = Request.Builder().url(normalizedUrl)

            // If file exists, try conditional GET
            if (destFile.exists() && destFile.length() > 0) {
                val lastModified = destFile.lastModified()
                if (lastModified > 0) {
                    val date = Date(lastModified)
                    val rfc1123 = synchronized(rfc1123Formatter) { rfc1123Formatter.format(date) }
                    requestBuilder.header("If-Modified-Since", rfc1123)
                }
            }

            val request = requestBuilder.build()

            MTOfflineHttpClient.client.newCall(request).execute().use { response ->
                val statusCode = response.code
                when (statusCode) {
                    304 -> {
                        // Not Modified, existing file is still valid.
                        // Update metadata if headers are present
                        updateMetadata(response, destFile)
                        return@withContext
                    }
                    204 -> return@withContext
                    in 200..299 -> {
                        val contentType = response.header("Content-Type")
                        validateContentType(contentType, resource.url)

                        val body = response.body ?: throw MTOfflineError.DownloadFailed(IOException("Empty response body"))

                        val data = body.bytes()
                        MTOfflineStorage.write(data, destFile)

                        // Update metadata
                        updateMetadata(response, destFile)
                    }
                    429 -> throw MTOfflineError.BadResponse(429)
                    in 500..599 -> throw MTOfflineError.BadResponse(statusCode)
                    else -> throw MTOfflineError.BadResponse(statusCode)
                }
            }
        }

    private suspend fun updateMetadata(
        response: okhttp3.Response,
        destFile: File,
    ) {
        val cacheControl = response.header("Cache-Control")
        val expires = response.header("Expires")

        var ttlSeconds: Long? = null

        // 1. Check max-age in Cache-Control
        if (cacheControl != null) {
            val maxAgePattern = java.util.regex.Pattern.compile("max-age=(\\d+)")
            val matcher = maxAgePattern.matcher(cacheControl)
            if (matcher.find()) {
                ttlSeconds = matcher.group(1)?.toLong()
            }
        }

        // 2. Check Expires header if max-age is not present
        if (ttlSeconds == null && expires != null) {
            try {
                val expiresDate = synchronized(rfc1123Formatter) { rfc1123Formatter.parse(expires) }
                if (expiresDate != null) {
                    ttlSeconds = (expiresDate.time - System.currentTimeMillis()) / 1000
                }
            } catch (e: Exception) {
                // Ignore parsing errors
            }
        }

        if (ttlSeconds != null && ttlSeconds > 0) {
            val expiresAt = System.currentTimeMillis() + (ttlSeconds * 1000)
            MTOfflineStorage.saveResourceMetadata(destFile, expiresAt)
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
