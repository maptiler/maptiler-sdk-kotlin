/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.helpers.JsonConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Represents a sprite defined in a map style.
 */
@Serializable
data class MTStyleSprite(
    /**
     * The identifier of the sprite. Default is "default" for string-based sprites.
     */
    val id: String = "default",
    /**
     * The URL of the sprite.
     */
    val url: String,
) {
    /**
     * Returns the URLs for the different scale variants of the sprite.
     *
     * @param scale The pixel scale (e.g., 1, 2, or 3).
     * @return A list of URLs for the JSON and PNG files of the sprite.
     */
    fun getVariantUrls(scale: Int = 1): List<String> {
        val scaleSuffix = if (scale > 1) "@${scale}x" else ""
        return listOf(
            "$url$scaleSuffix.json",
            "$url$scaleSuffix.png",
        )
    }
}

/**
 * Represents a tile source extracted from a style JSON.
 */
data class MTStyleSource(
    val id: String,
    val type: String? = null,
    val url: String? = null,
    val tiles: List<String>? = null,
    val minZoom: Int? = null,
    val maxZoom: Int? = null,
)

/**
 * Represents the non-tile dependencies extracted from a style JSON.
 */
data class MTStyleDependencies(
    /**
     * The sprites required by the style.
     */
    val sprites: List<MTStyleSprite>,
    /**
     * The glyphs template URL required by the style.
     */
    val glyphsTemplate: String?,
    /**
     * The tile sources required by the style.
     */
    val sources: List<MTStyleSource> = emptyList(),
    /**
     * The unique font stacks required by the style layers.
     */
    val fontStacks: List<List<String>> = emptyList(),
)

@Serializable
internal data class MTStyleRootRaw(
    val sprite: JsonElement? = null,
    val glyphs: String? = null,
    val sources: Map<String, MTStyleSourceRaw>? = null,
    val layers: List<MTStyleLayerRaw>? = null,
)

@Serializable
internal data class MTStyleSourceRaw(
    val type: String? = null,
    val url: String? = null,
    val tiles: List<String>? = null,
    val minzoom: Double? = null,
    val maxzoom: Double? = null,
)

@Serializable
internal data class MTStyleLayerRaw(
    val layout: MTStyleLayerLayoutRaw? = null,
)

@Serializable
internal data class MTStyleLayerLayoutRaw(
    @SerialName("text-font")
    val textFont: JsonElement? = null,
)

/**
 * A parser responsible for extracting offline dependencies (sprites and glyphs) from a style JSON payload.
 */
class MTStyleParser {
    /**
     * Parses the raw style JSON data to extract non-tile dependencies like sprites and glyphs.
     *
     * @param data The raw JSON string of the style.
     * @return An [MTStyleDependencies] object containing the extracted references.
     */
    fun extractDependencies(data: String): MTStyleDependencies {
        val root = JsonConfig.json.decodeFromString<MTStyleRootRaw>(data)

        val sprites = parseSprites(root.sprite)
        val glyphsTemplate = root.glyphs

        val sources = mutableListOf<MTStyleSource>()
        root.sources?.forEach { (id, rawSource) ->
            if (rawSource.type == "vector" || rawSource.type == "raster" || rawSource.type == "raster-dem") {
                if (rawSource.url != null || rawSource.tiles != null) {
                    sources.add(
                        MTStyleSource(
                            id = id,
                            type = rawSource.type,
                            url = rawSource.url,
                            tiles = rawSource.tiles,
                            minZoom = rawSource.minzoom?.toInt(),
                            maxZoom = rawSource.maxzoom?.toInt(),
                        ),
                    )
                }
            }
        }

        val uniqueFontStacks = mutableSetOf<List<String>>()
        root.layers?.forEach { layer ->
            val textFontElement = layer.layout?.textFont
            if (textFontElement != null) {
                try {
                    val fontStack = JsonConfig.json.decodeFromJsonElement<List<String>>(textFontElement)
                    if (!isExpression(fontStack)) {
                        uniqueFontStacks.add(fontStack)
                    }
                } catch (e: Exception) {
                    // Ignore if it's not a list of strings (e.g. an expression)
                }
            }
        }

        return MTStyleDependencies(
            sprites = sprites,
            glyphsTemplate = glyphsTemplate,
            sources = sources,
            fontStacks = uniqueFontStacks.toList(),
        )
    }

    private fun parseSprites(element: JsonElement?): List<MTStyleSprite> {
        if (element == null) return emptyList()

        return runCatching {
            // Check if it's a single string URL
            val url = JsonConfig.json.decodeFromJsonElement<String>(element)
            listOf(MTStyleSprite(id = "default", url = url))
        }.getOrElse {
            runCatching {
                // Check if it's an array of sprite objects
                JsonConfig.json.decodeFromJsonElement<List<MTStyleSprite>>(element)
            }.getOrElse {
                emptyList()
            }
        }
    }

    private fun isExpression(fontStack: List<String>): Boolean {
        val first = fontStack.firstOrNull() ?: return false
        val operators =
            setOf(
                "get", "has", "at", "in", "match", "case", "step", "interpolate", "coalesce", "let", "var", "literal",
            )
        return operators.contains(first)
    }
}
