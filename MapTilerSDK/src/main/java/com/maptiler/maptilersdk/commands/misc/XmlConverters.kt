/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig

internal data class GpxToGeoJSON(
    private val xml: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): JSString {
        val xmlString: JSString = JsonConfig.json.encodeToString(String.serializer(), xml)
        return "JSON.stringify(${MTBridge.SDK_OBJECT}.gpx($xmlString));"
    }
}

internal data class KmlToGeoJSON(
    private val xml: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): JSString {
        val xmlString: JSString = JsonConfig.json.encodeToString(String.serializer(), xml)
        return "JSON.stringify(${MTBridge.SDK_OBJECT}.kml($xmlString));"
    }
}
