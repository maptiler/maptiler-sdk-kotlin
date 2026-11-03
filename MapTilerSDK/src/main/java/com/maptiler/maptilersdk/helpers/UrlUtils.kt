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

private val secretQueryParamRegex = Regex("""([?&](?:key|mtsid)=)[^&#\s"')]*""", RegexOption.IGNORE_CASE)

// Replaces API key and session id query values in any URLs inside the text, so it is safe to log.
internal fun redactUrlSecrets(text: String): String = text.replace(secretQueryParamRegex, "$1REDACTED")
