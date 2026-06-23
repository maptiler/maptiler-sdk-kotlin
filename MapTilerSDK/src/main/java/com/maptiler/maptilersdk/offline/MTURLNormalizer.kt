/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.net.Uri
import com.maptiler.maptilersdk.MTConfig

/**
 * Normalizes MapTiler URLs for offline usage.
 */
internal object MTURLNormalizer {
    /**
     * Normalizes a URL by ensuring it uses the HTTPS scheme, points to the correct MapTiler API host,
     * and includes the API key from [MTConfig] if it isn't already present.
     *
     * @param url The URL to normalize.
     * @return The normalized URL.
     */
    fun normalize(url: String): String {
        val apiKey = MTConfig.apiKey
        return normalize(url, apiKey)
    }

    /**
     * Normalizes a URL by ensuring it uses the HTTPS scheme, points to the correct MapTiler API host,
     * and includes the provided API key as a query parameter if it isn't already present.
     *
     * @param url The URL to normalize.
     * @param apiKey The MapTiler API key.
     * @param sessionId Optional session ID to include in the URL.
     * @return The normalized URL.
     */
    fun normalize(
        url: String,
        apiKey: String,
        sessionId: String? = null,
    ): String {
        var uri = Uri.parse(url)

        // Handle custom maptiler:// scheme
        if (uri.scheme == "maptiler") {
            val originalHost = uri.host ?: ""
            val originalPath = uri.path ?: ""

            // Map maptiler://<host>/<path> to https://api.maptiler.com/<host>/<path>
            val builder =
                Uri.Builder()
                    .scheme("https")
                    .authority("api.maptiler.com")

            var newPath = "/$originalHost"
            if (originalPath.isNotEmpty()) {
                if (!originalPath.startsWith("/")) {
                    newPath += "/"
                }
                newPath += originalPath
            }
            builder.encodedPath(newPath)
            builder.encodedQuery(uri.encodedQuery)
            builder.fragment(uri.fragment)
            uri = builder.build()
        }

        val builder = uri.buildUpon()

        // Ensure query parameter "key" is present
        if (uri.getQueryParameter("key") == null) {
            builder.appendQueryParameter("key", apiKey)
        }

        // Ensure query parameter "mtsid" is present if sessionId is provided
        if (sessionId != null && sessionId.isNotEmpty() && uri.getQueryParameter("mtsid") == null) {
            builder.appendQueryParameter("mtsid", sessionId)
        }

        return builder.build().toString()
    }
}
