/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Predefined space cubemap presets.
 */
@Serializable
enum class MTSpacePreset {
    /** Dark blue background; stars stay white. Space color changes background color. */
    @SerialName("space")
    SPACE,

    /** Black background; space color changes stars color. */
    @SerialName("stars")
    STARS,

    /** Black half‑transparent background with standard milky way and stars. */
    @SerialName("milkyway")
    MILKYWAY,

    /** Subtle milky way, fewer stars. */
    @SerialName("milkyway-subtle")
    MILKYWAY_SUBTLE,

    /** Bright milky way, more stars. */
    @SerialName("milkyway-bright")
    MILKYWAY_BRIGHT,

    /** Full image with natural colors; space color has no effect. */
    @SerialName("milkyway-colored")
    MILKYWAY_COLORED,
}
