/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.annotations.MTRotationAlignment
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class SetMarkerRotationAlignment(
    val marker: MTMarker,
    val alignment: MTRotationAlignment,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${marker.identifier}.setRotationAlignment('${alignment.value}');"
}
