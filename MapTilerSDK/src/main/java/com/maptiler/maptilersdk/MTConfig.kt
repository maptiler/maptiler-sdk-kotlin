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
     * SDK version
     */
    const val VERSION = "2.4.0"

    /**
     * Custom User-Agent string for the SDK
     */
    var customUserAgent: String = "MapTiler-Mobile-SDK-Android/$VERSION"

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
     * This allows MapTiler to enable session-based billing.
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
     * API Key, MapTiler session ID, whether tile caching is enabled, whether a language is specified at initialization,
     * whether terrain is activated at initialization, and whether globe projection is activated at initialization.
     * Defaults to true.
     * @see <https://docs.maptiler.com/guides/maps-apis/maps-platform/what-is-map-session-in-maptiler-cloud/>
     */
    var isTelemetryEnabled: Boolean = true

    /**
     * Default MapTiler API host.
     */
    const val DEFAULT_API_HOST = "api.maptiler.com"

    /**
     * EU-based MapTiler API host.
     */
    const val EU_API_HOST = "api.maptiler.eu"

    /**
     * Switch between the default `api.maptiler.com` host and the EU-based `api.maptiler.eu` host.
     * Set to `false` to switch back to the default.
     *
     * Must be set before MTMapView is initialized and before offline downloads are started.
     * Defaults to false.
     */
    @Volatile
    var useEuEndpoints: Boolean = false

    /**
     * The host currently used for the MapTiler API requests (`api.maptiler.com` or `api.maptiler.eu`).
     */
    val apiHost: String
        get() = if (useEuEndpoints) EU_API_HOST else DEFAULT_API_HOST

    /**
     * The base URL currently used for the MapTiler API requests
     * (`https://api.maptiler.com/` or `https://api.maptiler.eu/`).
     */
    val apiURL: String
        get() = "https://$apiHost/"

    /**
     * Sets a custom application identifier to be prepended to the SDK's User-Agent string.
     * This allows you to restrict your MapTiler API key to specific applications.
     *
     * Must be called before MTMapView is initialized and before offline downloads are started.
     * @param identifier The application identifier (e.g., your application ID).
     * Only alphanumeric characters, periods, hyphens, and underscores are allowed.
     */
    fun setApplicationIdentifier(identifier: String) {
        val allowedChars = setOf('-', '.', '_')
        val sanitized = identifier.filter { it.isLetterOrDigit() || allowedChars.contains(it) }

        if (sanitized.isNotEmpty()) {
            customUserAgent = "$sanitized MapTiler-Mobile-SDK-Android/$VERSION"
        }
    }
}
