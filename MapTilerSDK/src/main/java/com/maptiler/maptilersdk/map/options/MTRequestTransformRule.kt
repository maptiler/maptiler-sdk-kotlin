/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import kotlinx.serialization.Serializable

/**
 * A rule that defines how to transform a request for a map resource.
 * The rule is applied if the requested URL matches either [matchUrlPrefix] or [matchUrlRegex].
 *
 * @param matchUrlPrefix URL prefix to match.
 * @param matchUrlRegex Regex string to match the URL.
 * @param action The action to apply if the URL matches.
 */
@Serializable
data class MTRequestTransformRule(
    val matchUrlPrefix: String? = null,
    val matchUrlRegex: String? = null,
    val action: MTRequestTransformAction,
)
