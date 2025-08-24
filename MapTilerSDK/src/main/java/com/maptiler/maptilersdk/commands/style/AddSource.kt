/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.map.style.source.MTSource
import com.maptiler.maptilersdk.map.style.source.MTVectorTileSource

internal data class AddSource(
    val source: MTSource,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String =
        if (source is MTVectorTileSource) {
            handleMTVectorTileSource(source)
        } else {
            ""
        }

    private fun handleMTVectorTileSource(source: MTVectorTileSource): JSString {
        var data: JSString = ""

        if (source.url != null) {
            data = "url: '${source.url!!}'"
        } else if (source.tiles != null) {
            data = "tiles: ${source.tiles!!}}"
        }

        var attributionString: JSString = ""
        if (source.attribution != null) {
            attributionString = "attribution: '${source.attribution}'"
        }

        return """
        ${MTBridge.MAP_OBJECT}.addSource('${source.identifier}', {
            type: '${source.type}',
            minzoom: ${source.minZoom},
            maxzoom: ${source.maxZoom},
            scheme: '${source.scheme}',
            bounds: ${source.bounds.contentToString()},
            $data,
            $attributionString
        });
        """
    }
}
