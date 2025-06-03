/**
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.logging

/**
 * Type of log messages in the SDK.
 */
enum class MTLogType {
    /**
     * Informational messages.
     */
    INFO,

    /**
     * Warning messages.
     */
    WARNING,

    /**
     * SDK Errors.
     */
    ERROR,

    /**
     * Critical SDK errors.
     */
    CRITICAL_ERROR,

    /**
     * Event messages.
     */
    EVENT,
}
