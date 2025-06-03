/**
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk

import com.maptiler.maptilersdk.logging.MTLogLevel

/**
 * Object representing the SDK global settings.
 *
 * Exposes properties and options such as API Key and caching preferences.
 */
object MTConfig {
    /**
     * SDK log level.
     */
    var logLevel: MTLogLevel = MTLogLevel.None
}
