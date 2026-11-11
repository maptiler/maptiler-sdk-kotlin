/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class RemoveMarkers(
    val markers: List<MTMarker>,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String =
        markers.joinToString("\n") {
            "if (typeof ${it.identifier} !== 'undefined') { ${RemoveMarker(it).toJS()} }"
        }
}
