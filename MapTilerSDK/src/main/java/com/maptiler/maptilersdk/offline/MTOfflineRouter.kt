/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
import java.io.File

/**
 * Handles routing of offline asset requests to local file system paths.
 */
internal class MTOfflineRouter(internal val context: Context) {
    /**
     * Represents a resolved offline resource.
     */
    data class ResolvedResource(
        val file: File,
        val mimeType: String,
    )

    /**
     * Resolves a virtual path to a local file and its MIME type.
     *
     * @param path The virtual path, expected to start with "/offline/".
     * @return The resolved resource, or null if not found or invalid.
     */
    fun resolve(path: String): ResolvedResource? {
        if (!path.startsWith("/offline/")) {
            return null
        }

        // Strip the "/offline/" prefix
        val relativePath = path.substring("/offline/".length)
        val components = relativePath.split("/")

        // Minimum expected: <packID>/<resource>
        if (components.size < 2) {
            return null
        }

        val packId = components[0]
        val resourcePath = components.drop(1).joinToString("/")

        // Check if the pack is past its grace period.
        // If it is, we should not serve resources from it anymore.
        val isValid =
            try {
                val metadata = MTOfflineStorage.loadMetadataBlocking(context, packId)
                metadata == null || !metadata.isPastGracePeriod
            } catch (e: Exception) {
                true // Fallback to allowing if metadata is unreadable
            }

        if (!isValid) {
            return null
        }

        val packDir = MTOfflineStoragePaths.getPackDirectory(context, packId)

        return when {
            resourcePath == "style.json" -> {
                val file = File(packDir, "style.json")
                if (file.exists()) ResolvedResource(file, "application/json") else null
            }

            resourcePath.startsWith("sprite") -> {
                val file = File(packDir, resourcePath)
                if (file.exists()) {
                    val mimeType = if (resourcePath.endsWith(".json")) "application/json" else "image/png"
                    ResolvedResource(file, mimeType)
                } else {
                    null
                }
            }

            resourcePath.startsWith("glyphs/") -> {
                resolveGlyphs(packDir, resourcePath.substring("glyphs/".length))
            }

            resourcePath.startsWith("tiles/") -> {
                resolveTiles(packDir, resourcePath.substring("tiles/".length))
            }

            else -> null
        }
    }

    private fun resolveGlyphs(
        packDir: File,
        glyphPath: String,
    ): ResolvedResource? {
        // glyphPath format: {fontstack}/{range}.pbf
        val components = glyphPath.split("/")
        if (components.size != 2) return null

        val fontstack = components[0]
        val rangePbf = components[1]

        // Handle fontstack lists by picking the first available font directory.
        val fonts = fontstack.split(",").map { it.trim() }
        val glyphsDir = File(packDir, "glyphs")

        for (font in fonts) {
            val fontDir = File(glyphsDir, font)
            val file = File(fontDir, rangePbf)
            if (file.exists()) {
                return ResolvedResource(file, "application/x-protobuf")
            }
        }

        return null
    }

    private fun resolveTiles(
        packDir: File,
        tilePath: String,
    ): ResolvedResource? {
        // tilePath format: {sourceId}/{z}/{x}/{y}.{ext}
        val components = tilePath.split("/")
        if (components.size != 4) return null

        val sourceId = components[0]
        val z = components[1]
        val x = components[2]
        val yWithExt = components[3]

        val tilesDir = File(packDir, "tiles")
        val sourceDir = File(tilesDir, sourceId)
        val zDir = File(sourceDir, z)
        val xDir = File(zDir, x)
        val file = File(xDir, yWithExt)

        if (!file.exists()) return null

        val ext = file.extension.lowercase()
        return ResolvedResource(file, getMimeType(ext))
    }

    private fun getMimeType(extension: String): String {
        return when (extension) {
            "json" -> "application/json"
            "png" -> "image/png"
            "pbf" -> "application/x-protobuf"
            "webp" -> "image/webp"
            "jpg", "jpeg" -> "image/jpeg"
            else -> "application/octet-stream"
        }
    }
}
