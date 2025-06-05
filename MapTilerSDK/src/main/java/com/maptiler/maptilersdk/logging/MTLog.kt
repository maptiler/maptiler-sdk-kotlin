/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.logging

/**
 * Log object used with MTLogger.
 * @property message Log message.
 * @property type Type of the log.
 */
data class MTLog(
    val message: String,
    val type: MTLogType,
) {
    /**
     * Prints the log to the console.
     */
    fun printLog() {
        println("$type: $message")
    }
}
