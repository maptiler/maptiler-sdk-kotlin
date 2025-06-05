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
    private var apiKey: String = ""
    private var unit: MTUnit = MTUnit.METRIC

    /**
     * SDK log level.
     */
    var logLevel: MTLogLevel = MTLogLevel.None
        private set

    /**
     * Boolean indicating whether caching is enabled.
     *
     * Defaults to true.
     */
    var isCachingEnabled: Boolean = true
        private set

    /**
     * Boolean indicating whether session logic is enabled.
     *
     * This allows MapTiler to enable "session based billing".
     * Defaults to true.
     * For more information about sessions, see [Map Sessions](https://docs.maptiler.com/guides/maps-apis/maps-platform/what-is-map-session-in-maptiler-cloud/).
     */
    var isSessionLogicEnabled: Boolean = true
        private set

    /**
     * Boolean indicating whether telemetry is enabled.
     *
     * The telemetry is very valuable to the team at MapTiler because it shares information about
     * where to add the extra effort. It also helps spotting some incompatibility issues that may
     * arise between the SDK and a specific version of a module. It consist in sending the SDK version,
     * API Key, MapTiler session ID, if tile caching is enabled, if language specified at initialization,
     * if terrain is activated at initialization, if globe projection is activated at initialization.
     * Defaults to true.
     * @see 'https://docs.maptiler.com/guides/maps-apis/maps-platform/what-is-map-session-in-maptiler-cloud/'
     */
    var isTelemetryEnabled: Boolean = true
        private set

    /**
     * Sets the MapTiler API key.
     *
     * @param apiKey The MapTiler API Key.
     */
    fun setAPIKey(apiKey: String) {
        this.apiKey = apiKey
    }

    /**
     * Returns the MapTiler API key.
     */
    fun getAPIKey(): String {
        return this.apiKey
    }

    /**
     * Sets the caching mechanism.
     *
     * Enabled by default.
     *
     * @param isEnabled Boolean indicating whether caching is enabled.
     */
    fun setCaching(isEnabled: Boolean) {
        this.isCachingEnabled = isEnabled
    }

    /**
     * Sets the session logic.
     *
     * Make sure to call before map init or use MTMapOptions to supply before map init.
     * Enabled by default.
     *
     * @param isEnabled Boolean indicating whether session logic is enabled.
     */
    fun setSessionLogic(isEnabled: Boolean) {
        this.isSessionLogicEnabled = isEnabled
    }

    /**
     * Sets the unit of measurement.
     *
     * Defaults to METRIC.
     *
     * @param unit The MTUnit type.
     */
    fun setUnit(unit: MTUnit) {
        this.unit = unit
    }

    /**
     * Returns the unit of measurement.
     */
    fun getUnit(): MTUnit {
        return this.unit
    }

    /**
     * Sets the telemetry.
     *
     * Enabled by default.
     *
     * @param isEnabled Boolean indicating whether telemetry is enabled.
     */
    fun setTelemetry(isEnabled: Boolean) {
        this.isTelemetryEnabled = isEnabled
    }
}
