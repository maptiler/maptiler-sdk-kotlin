/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.source

import java.net.URL

/**
 * Protocol requirements for all types of Sources.
 */

interface MTSource {
    /**
     * Unique id of the source.
     */
    var identifier: String

    /**
     * URL pointing to the source resource.
     */
    var url: URL?

    /**
     *  Type of the source.
     */
    val type: MTSourceType
}
