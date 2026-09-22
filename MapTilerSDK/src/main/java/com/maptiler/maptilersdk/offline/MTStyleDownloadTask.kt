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
import java.util.Locale
import java.util.TimeZone

/**
 * A concrete download task that fetches the map style.
 */
internal class MTStyleDownloadTask(
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
        val retryPolicy = MTNetworkRetryPolicy(maxAttempts = 5)

        try {
            retryPolicy.execute {
                MTConnectivity.suspendUntilNetworkAvailable(context)
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
                        val body = response.body ?: throw MTOfflineError.DownloadFailed(IOException("Empty response body"))
                        val data = body.bytes()

                        val destFile = destinationFile ?: return@withContext
                        MTOfflineStorage.write(data, destFile)
                    }
                    429 -> {
                        val retryAfterStr = response.header("Retry-After")
                        var retryAfterSeconds: Long? = null
                        if (retryAfterStr != null) {
                            val seconds = retryAfterStr.toLongOrNull()
                            if (seconds != null) {
                                retryAfterSeconds = seconds
                            } else {
                                try {
                                    val date = synchronized(rfc1123Formatter) { rfc1123Formatter.parse(retryAfterStr) }
                                    if (date != null) {
                                        val delay = (date.time - System.currentTimeMillis()) / 1000
                                        retryAfterSeconds = if (delay > 0) delay else 0
                                    }
                                } catch (e: Exception) {
                                    // Ignore parse errors
                                }
                            }
                        }
                        throw MTOfflineError.RateLimitExceeded(retryAfterSeconds)
                    }
                    in 500..599 -> throw MTOfflineError.BadResponse(statusCode)
                    else -> throw MTOfflineError.BadResponse(statusCode)
                }
            }
        }
}
