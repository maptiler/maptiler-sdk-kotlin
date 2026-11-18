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

internal data class RemoveFeatureState(
    val feature: MTFeatureIdentifier,
    val key: String? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val featureString = JsonConfig.json.encodeToString(feature)
        return if (key != null) {
            "${MTBridge.MAP_OBJECT}.removeFeatureState($featureString, \"$key\");"
        } else {
            "${MTBridge.MAP_OBJECT}.removeFeatureState($featureString);"
        }
    }
}
