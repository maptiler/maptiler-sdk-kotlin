/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import kotlinx.serialization.json.Json

object JsonConfig {
    val json =
        Json {
            prettyPrint = true
            encodeDefaults = true
            explicitNulls = false
        }
}
