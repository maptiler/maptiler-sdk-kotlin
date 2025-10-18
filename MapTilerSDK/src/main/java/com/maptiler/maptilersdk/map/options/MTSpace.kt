/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.options

import com.maptiler.maptilersdk.map.style.layer.fill.ColorAsHexSerializer
import kotlinx.serialization.Serializable

/**
 * Space configuration for customizing the globe background (deep space/skybox effects).
 * Requires globe projection to be visible.
 */
@Serializable
class MTSpace {
    /** Solid color for the space background or for tinting presets. */
    @Serializable(with = ColorAsHexSerializer::class)
    var color: Int? = null
        private set

    /** Predefined cubemap preset. */
    var preset: MTSpacePreset? = null
        private set

    /** Custom cubemap faces. */
    var faces: MTSpaceFaces? = null
        private set

    /** Path-based cubemap definition. */
    var path: MTSpacePath? = null
        private set

    constructor(
        color: Int? = null,
        preset: MTSpacePreset? = null,
        faces: MTSpaceFaces? = null,
        path: MTSpacePath? = null,
    ) {
        this.color = color
        this.preset = preset
        this.faces = faces
        this.path = path
    }
}
