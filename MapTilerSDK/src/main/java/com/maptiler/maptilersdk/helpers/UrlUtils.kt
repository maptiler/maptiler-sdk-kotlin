/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import java.net.URL

internal fun formatUrlForJs(url: URL): String {
    return if (url.protocol.equals("file", ignoreCase = true) && url.authority.isNullOrEmpty()) {
        // Ensure triple-slash form for file URLs without authority: file:///path
        "file://" + url.path
    } else {
        url.toString()
    }
}
