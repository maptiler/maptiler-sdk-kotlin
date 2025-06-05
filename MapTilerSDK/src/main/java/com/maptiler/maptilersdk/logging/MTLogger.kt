/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.logging

/**
 * Protocol requirement for all Logger objects.
 */
interface MTLoggable {
    fun log(
        message: String,
        type: MTLogType,
    )
}

/**
 * Logger class used for SDK logs.
 */
object MTLogger {
    /**
     * Log marker for information messages.
     */
    const val INFO_MARKER = "MTInfo"

    /**
     * Adds a log.
     */
    fun log(
        message: String,
        type: MTLogType,
    ) {
        MTLoggerInternal.log(message, type)
    }

    /**
     * Prints all current logs to the console.
     */
    fun printLogs() {
        MTLoggerInternal.printLogs()
    }

    /**
     * Injects a custom logger conforming to the MTLoggable protocol.
     */
    fun setCustomLogger(logger: MTLoggable) {
        MTLoggerInternal.setCustomLogger(logger)
    }
}

private object MTLoggerInternal {
    private object Constants {
        const val MAX_LOG_COUNT = 8000
        const val MAX_COUNT_EXCEEDED_MESSAGE =
            "Maximum in-memory log count exceeded, earliest logs now accessible only through Logcat."
    }

    private var logger: MTLoggable = OSLogger()
    private val logs = mutableListOf<MTLog>()

    fun log(
        message: String,
        type: MTLogType,
    ) {
        val log = MTLog(message, type)
        handleLog(log)
        logger.log(message, type)
    }

    fun printLogs() {
        logs.forEach { println(it) }
    }

    fun setCustomLogger(logger: MTLoggable) {
        this.logger = logger
    }

    private fun handleLog(log: MTLog) {
        logs.add(log)

        if (logs.size > Constants.MAX_LOG_COUNT) {
            logger.log(Constants.MAX_COUNT_EXCEEDED_MESSAGE, MTLogType.WARNING)
            logs.removeAt(0)
        }
    }
}
