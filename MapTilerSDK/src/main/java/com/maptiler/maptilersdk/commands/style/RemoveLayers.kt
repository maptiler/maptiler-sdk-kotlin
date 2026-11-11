/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.style.layer.MTLayer

internal data class RemoveLayers(
    val layers: List<MTLayer>,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = layers.joinToString("\n") { RemoveLayer(it).toJS() }
}
