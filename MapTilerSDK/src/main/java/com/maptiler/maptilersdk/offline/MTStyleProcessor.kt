/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * A utility responsible for transforming a map style JSON to use local offline resources.
 */
internal class MTStyleProcessor(
    private val baseURL: String,
    private val packId: String,
) {
    /**
     * Transforms the given style JSON into an offline-ready version.
     *
     * @param style The original style as a [JsonObject].
     * @param maxZoom Optional max zoom level to force on vector/raster sources.
     * @return The transformed style as a [JsonObject].
     */
    fun transform(
        style: JsonObject,
        maxZoom: Int? = null,
    ): JsonObject {
        val mutableStyle = style.toMutableMap()

        rewriteSprites(mutableStyle)
        rewriteGlyphs(mutableStyle)

        val purgedSourceIds = mutableSetOf<String>()
        rewriteSources(mutableStyle, purgedSourceIds, maxZoom)
        rewriteLayers(mutableStyle, purgedSourceIds)

        return JsonObject(mutableStyle)
    }

    private fun rewriteSprites(style: MutableMap<String, JsonElement>) {
        val sprite = style["sprite"] ?: return

        when (sprite) {
            is JsonPrimitive -> {
                style["sprite"] = JsonPrimitive("$baseURL/offline/$packId/sprite")
            }
            is JsonArray -> {
                style["sprite"] =
                    buildJsonArray {
                        sprite.forEach { element ->
                            val obj = element.jsonObject
                            val id = obj["id"]?.jsonPrimitive?.content
                            val suffix = if (id == null || id == "default") "" else "-$id"
                            add(
                                buildJsonObject {
                                    obj.forEach { (key, value) ->
                                        if (key == "url") {
                                            put(key, "$baseURL/offline/$packId/sprite$suffix")
                                        } else {
                                            put(key, value)
                                        }
                                    }
                                },
                            )
                        }
                    }
            }
            else -> {}
        }
    }

    private fun rewriteGlyphs(style: MutableMap<String, JsonElement>) {
        if (style.containsKey("glyphs")) {
            style["glyphs"] = JsonPrimitive("$baseURL/offline/$packId/glyphs/{fontstack}/{range}.pbf")
        }
    }

    private fun rewriteSources(
        style: MutableMap<String, JsonElement>,
        purgedSourceIds: MutableSet<String>,
        maxZoom: Int?,
    ) {
        val sources = style["sources"]?.jsonObject ?: return
        val mutableSources = sources.toMutableMap()

        val sourcesToPurge = listOf("maptiler_attribution")
        sourcesToPurge.forEach { id ->
            if (mutableSources.remove(id) != null) {
                purgedSourceIds.add(id)
            }
        }

        mutableSources.forEach { (sourceId, source) ->
            mutableSources[sourceId] = transformSource(sourceId, source.jsonObject, maxZoom)
        }

        style["sources"] = JsonObject(mutableSources)
    }

    private fun transformSource(
        sourceId: String,
        source: JsonObject,
        maxZoom: Int?,
    ): JsonObject {
        val type = source["type"]?.jsonPrimitive?.content
        if (type != "vector" && type != "raster" && type != "raster-dem") {
            return source
        }

        val mutableSource = source.toMutableMap()
        mutableSource.remove("url")
        mutableSource.remove("scheme")

        if (maxZoom != null) {
            mutableSource["maxzoom"] = JsonPrimitive(maxZoom)
        }

        val ext = determineExtension(source)
        mutableSource["tiles"] =
            buildJsonArray {
                add("$baseURL/offline/$packId/tiles/$sourceId/{z}/{x}/{y}.$ext")
            }

        return JsonObject(mutableSource)
    }

    private fun determineExtension(source: JsonObject): String {
        val type = source["type"]?.jsonPrimitive?.content ?: return "pbf"

        if (type == "vector") return "pbf"

        val tiles = source["tiles"]?.jsonArray
        val firstTile = tiles?.firstOrNull()?.jsonPrimitive?.content?.lowercase()

        if (firstTile != null) {
            if (firstTile.contains(".png")) return "png"
            if (firstTile.contains(".jpg") || firstTile.contains(".jpeg")) return "jpg"
            if (firstTile.contains(".webp")) return "webp"
        }

        val url = source["url"]?.jsonPrimitive?.content?.lowercase()
        if (url != null && url.contains("satellite")) {
            return "jpg"
        }

        return "png"
    }

    private fun rewriteLayers(
        style: MutableMap<String, JsonElement>,
        purgedSourceIds: Set<String>,
    ) {
        val layers = style["layers"]?.jsonArray ?: return
        style["layers"] =
            buildJsonArray {
                layers.forEach { layer ->
                    val sourceId = layer.jsonObject["source"]?.jsonPrimitive?.content
                    if (sourceId == null || !purgedSourceIds.contains(sourceId)) {
                        add(layer)
                    }
                }
            }
    }
}
