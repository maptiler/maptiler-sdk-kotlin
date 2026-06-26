/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

/**
 * Interface for retry policies.
 */
internal interface MTRetryPolicy {
    /**
     * Executes the given [operation] with retry logic.
     *
     * @param operation The suspending operation to execute.
     * @return The result of the operation.
     * @throws Exception if the operation fails after all retries.
     */
    suspend fun <T> execute(operation: suspend () -> T): T
}

/**
 * A retry policy specifically designed for network operations.
 * Implements exponential backoff with jitter and handles HTTP 429 (Too Many Requests).
 */
internal class MTNetworkRetryPolicy(
    private val maxAttempts: Int = 3,
    private val baseDelayMillis: Long = 1000L,
    private val maxDelayMillis: Long = 60000L,
) : MTRetryPolicy {
    override suspend fun <T> execute(operation: suspend () -> T): T {
        var attempt = 1
        while (true) {
            try {
                return operation()
            } catch (e: Exception) {
                if (attempt >= maxAttempts || !isRetryable(e)) {
                    throw e
                }

                val delayMillis = calculateDelay(e, attempt)
                delay(delayMillis)
                attempt++
            }
        }
    }

    private fun isRetryable(e: Exception): Boolean {
        return when (e) {
            is MTOfflineError.BadResponse -> {
                when (e.statusCode) {
                    429 -> true // Too Many Requests
                    in 500..599 -> true // Server errors
                    else -> false
                }
            }
            is MTOfflineError.NetworkError -> true
            is IOException -> true
            else -> false
        }
    }

    private fun calculateDelay(
        e: Exception,
        attempt: Int,
    ): Long {
        // Handle 429 Retry-After if we were to pass it in the exception,
        // but for now let's use exponential backoff.

        val exponentialDelay = baseDelayMillis * 2.0.pow(attempt - 1).toLong()
        val maxAllowedDelay = min(exponentialDelay, maxDelayMillis)

        // Jitter: random value between 0.5 * delay and 1.5 * delay
        val jitterFactor = Random.nextDouble(0.5, 1.5)
        return (maxAllowedDelay * jitterFactor).toLong()
    }
}
