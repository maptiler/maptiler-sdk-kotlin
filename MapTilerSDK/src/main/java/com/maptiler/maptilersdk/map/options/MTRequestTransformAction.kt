/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import kotlinx.serialization.Serializable

/**
 * An action to apply to a URL if it matches a [MTRequestTransformRule].
 *
 * @param addQueryParameters Query parameters to add to the URL.
 * @param addHeaders Headers to add to the request.
 * @param rewriteUrl String to replace the matched prefix or regex in the URL.
 * @param setCredentials Credentials policy to use for the request (e.g. "include", "same-origin").
 */
@Serializable
data class MTRequestTransformAction(
    val addQueryParameters: Map<String, String>? = null,
    val addHeaders: Map<String, String>? = null,
    val rewriteUrl: String? = null,
    val setCredentials: String? = null,
)
