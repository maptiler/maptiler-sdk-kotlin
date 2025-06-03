/**
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.logging

/**
 * SDK log level.
 */
sealed class MTLogLevel {
    /**
     * No logs will be printed.
     */
    object None : MTLogLevel()

    /**
     * Only information logs will be printed.
     */
    object Info : MTLogLevel()

    /**
     * All logs will be printed.
     */
    data class Debug(val verbose: Boolean = false) : MTLogLevel()

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            this is None && other is None -> true
            this is Info && other is Info -> true
            this is Debug && other is Debug -> this.verbose == other.verbose
            else -> false
        }
    }

    override fun hashCode(): Int {
        return when (this) {
            is None -> 0
            is Info -> 1
            is Debug -> 2 + verbose.hashCode()
        }
    }
}
