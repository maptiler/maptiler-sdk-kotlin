/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.types.MTFeatureIdentifier
import kotlinx.serialization.encodeToString

internal data class GetFeatureState(
    val feature: MTFeatureIdentifier,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String {
        val featureString = JsonConfig.json.encodeToString(feature)
        return "JSON.stringify(${MTBridge.MAP_OBJECT}.getFeatureState($featureString));"
    }
}
