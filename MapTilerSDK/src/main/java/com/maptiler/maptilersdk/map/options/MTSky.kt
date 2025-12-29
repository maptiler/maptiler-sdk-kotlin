/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import com.maptiler.maptilersdk.map.style.dsl.PropertyValue
import com.maptiler.maptilersdk.map.style.dsl.StyleValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Sky configuration for customizing atmospheric appearance.
 *
 * All properties are optional; unspecified fields keep their previous values. Colors accept CSS strings or hex int
 * values. Blend values are clamped to [0, 1] when provided as plain numbers.
 */
@Serializable
data class MTSky(
    @SerialName("sky-color")
    val skyColor: StyleValue? = null,
    @SerialName("sky-horizon-blend")
    val skyHorizonBlend: StyleValue? = null,
    @SerialName("horizon-color")
    val horizonColor: StyleValue? = null,
    @SerialName("horizon-fog-blend")
    val horizonFogBlend: StyleValue? = null,
    @SerialName("fog-color")
    val fogColor: StyleValue? = null,
    @SerialName("fog-ground-blend")
    val fogGroundBlend: StyleValue? = null,
    @SerialName("atmosphere-blend")
    val atmosphereBlend: StyleValue? = null,
) {
    companion object {
        /** Helper for supplying a hex color int. */
        fun color(value: Int): StyleValue = StyleValue.Color(value)

        /** Helper for supplying a CSS-compatible color string (e.g., "#RRGGBB" or "rgba(...)"). */
        fun color(value: String): StyleValue = StyleValue.Str(value)

        /** Helper for supplying a numeric blend value. */
        fun number(value: Double): StyleValue = StyleValue.Number(value)

        /** Helper for supplying an expression via [PropertyValue]. */
        fun expression(value: PropertyValue): StyleValue = StyleValue.Expression(value)
    }
}
