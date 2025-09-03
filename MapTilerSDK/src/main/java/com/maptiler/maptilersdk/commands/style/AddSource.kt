/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.style.source.MTGeoJSONSource
import com.maptiler.maptilersdk.map.style.source.MTSource
import com.maptiler.maptilersdk.map.style.source.MTVectorTileSource

internal data class AddSource(
    val source: MTSource,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String =
        if (source is MTVectorTileSource) {
            handleMTVectorTileSource(source)
        } else if (source is MTGeoJSONSource) {
            handleGeoJSONSource(source)
        } else {
            ""
        }

    private fun handleMTVectorTileSource(source: MTVectorTileSource): JSString {
        val props = mutableListOf<String>()

        // Use serialized enum value (lowercase, per style spec)
        props += "type: '${source.type}'"
        props += "minzoom: ${source.minZoom}"
        props += "maxzoom: ${source.maxZoom}"
        props += "scheme: '${source.scheme}'"
        props += "bounds: ${source.bounds.contentToString()}"

        if (source.url != null) {
            props += "url: '${source.url!!}'"
        } else if (source.tiles != null) {
            val urls: List<String> = source.tiles!!.map { it.toString() }
            val tilesString: JSString = JsonConfig.json.encodeToString(urls)
            props += "tiles: $tilesString"
        }

        if (source.attribution != null) {
            props += "attribution: '${source.attribution}'"
        }

        val propsString = props.joinToString(",\n            ")

        return """
        ${MTBridge.MAP_OBJECT}.addSource('${source.identifier}', {
            $propsString
        });
        """
    }

    private fun handleGeoJSONSource(source: MTGeoJSONSource): JSString {
        val sourceString: JSString = JsonConfig.json.encodeToString(source)

        val jsSourceString =
            when {
                source.url?.protocol == "file" -> replaceDataString(sourceString)
                !source.jsonString.isNullOrEmpty() -> replaceDataString(sourceString)
                else -> sourceString
            }
        return "${MTBridge.MAP_OBJECT}.addSource('${source.identifier}', $jsSourceString);"
    }

    private fun replaceDataString(sourceString: String): String {
        val regex = Regex("""("data":\s*)"([^"]*)"""")
        return regex.replace(sourceString, "$1$2")
    }
}
