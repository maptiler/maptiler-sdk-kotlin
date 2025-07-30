/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class SetCoordinatesToMarker(
    val marker: MTMarker,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${marker.identifier}.setLngLat([${marker.coordinates.lng}, ${marker.coordinates.lat}]);"
}
