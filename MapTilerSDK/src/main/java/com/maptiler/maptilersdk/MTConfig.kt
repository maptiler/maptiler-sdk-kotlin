/*
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
     * MapTiler API Key
     */
    var apiKey: String = ""

    /**
     * Unit of measurement.
     */
    var unit: MTUnit = MTUnit.METRIC

    /**
     * SDK log level.
     */
    var logLevel: MTLogLevel = MTLogLevel.None

    /**
     * Boolean indicating whether caching is enabled.
     *
     * Defaults to true.
     */
    var isCachingEnabled: Boolean = true

    /**
     * Boolean indicating whether session logic is enabled.
     *
     * This allows MapTiler to enable "session based billing".
     * Defaults to true.
     * For more information about sessions, see [Map Sessions](https://docs.maptiler.com/guides/maps-apis/maps-platform/what-is-map-session-in-maptiler-cloud/).
     */
    var isSessionLogicEnabled: Boolean = true

    /**
     * Boolean indicating whether telemetry is enabled.
     *
     * The telemetry is very valuable to the team at MapTiler because it shares information about
     * where to add extra effort. It also helps spot some incompatibility issues that may
     * arise between the SDK and a specific version of a module. It consists of sending the SDK version,
     * API Key, MapTiler session ID, if tile caching is enabled, if language specified at initialization,
     * if terrain is activated at initialization, if globe projection is activated at initialization.
     * Defaults to true.
     * @see 'https://docs.maptiler.com/guides/maps-apis/maps-platform/what-is-map-session-in-maptiler-cloud/'
     */
    var isTelemetryEnabled: Boolean = true
}
