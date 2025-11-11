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
import com.maptiler.maptilersdk.helpers.formatUrlForJs
import com.maptiler.maptilersdk.map.style.source.MTGeoJSONSource
import com.maptiler.maptilersdk.map.style.source.MTSource
import com.maptiler.maptilersdk.map.style.source.MTVectorTileSource
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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
        val obj =
            buildJsonObject {
                put("type", JsonPrimitive(source.type.toString().lowercase()))

                // Set data from URL or inline JSON string
                when {
                    source.url != null -> put("data", JsonPrimitive(formatUrlForJs(source.url!!)))
                    !source.jsonString.isNullOrBlank() -> {
                        // Parse inline GeoJSON into a JsonElement so it serializes as an object, not a string
                        val dataEl = Json.parseToJsonElement(source.jsonString!!)
                        put("data", dataEl)
                    }
                }

                source.attribution?.let { put("attribution", JsonPrimitive(it)) }
                source.buffer?.let { put("buffer", JsonPrimitive(it)) }
                // Include cluster flag only when true to keep payload concise
                if (source.isCluster) put("cluster", JsonPrimitive(true))
                source.clusterMaxZoom?.let { put("clusterMaxZoom", JsonPrimitive(it)) }
                source.clusterRadius?.let { put("clusterRadius", JsonPrimitive(it)) }
                source.maxZoom?.let { put("maxzoom", JsonPrimitive(it)) }
                source.tolerance?.let { put("tolerance", JsonPrimitive(it)) }
                source.lineMetrics?.let { put("lineMetrics", JsonPrimitive(it)) }
            }

        val jsSourceString = JsonConfig.json.encodeToString(JsonObject.serializer(), obj)
        return "${MTBridge.MAP_OBJECT}.addSource('${source.identifier}', $jsSourceString);"
    }

    // shared helper in helpers package
}
