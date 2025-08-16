/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.style.source.MTSource
import java.net.URL

internal data class SetTilesToSource(
    val tiles: Array<URL>,
    val source: MTSource,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${MTBridge.MAP_OBJECT}.getSource('${source.identifier}').setTiles($tiles;"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SetTilesToSource

        if (!tiles.contentEquals(other.tiles)) return false
        if (source != other.source) return false
        if (isPrimitiveReturnType != other.isPrimitiveReturnType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tiles.contentHashCode()
        result = 31 * result + source.hashCode()
        result = 31 * result + isPrimitiveReturnType.hashCode()
        return result
    }
}
