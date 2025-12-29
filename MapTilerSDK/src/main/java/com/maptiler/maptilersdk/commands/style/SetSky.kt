/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.options.MTSky
import com.maptiler.maptilersdk.map.style.dsl.StyleValue

/**
 * Sets the sky configuration on the map.
 * Unspecified fields keep their previous values.
 */
internal data class SetSky(
    val sky: MTSky,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val sanitizedSky =
            sky.copy(
                skyHorizonBlend = clampBlend(sky.skyHorizonBlend),
                horizonFogBlend = clampBlend(sky.horizonFogBlend),
                fogGroundBlend = clampBlend(sky.fogGroundBlend),
                atmosphereBlend = clampBlend(sky.atmosphereBlend),
            )
        val json = JsonConfig.json.encodeToString(sanitizedSky)
        return "${MTBridge.MAP_OBJECT}.setSky($json);"
    }

    private fun clampBlend(value: StyleValue?): StyleValue? =
        when (value) {
            is StyleValue.Number -> {
                val numeric = value.value.takeIf { it.isFinite() } ?: 0.0
                StyleValue.Number(numeric.coerceIn(0.0, 1.0))
            }
            else -> value
        }
}
