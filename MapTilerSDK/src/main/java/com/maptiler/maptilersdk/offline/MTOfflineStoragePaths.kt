/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import android.content.Context
import java.io.File

/**
 * Centralizes path resolution for offline packs.
 */
internal object MTOfflineStoragePaths {
    private var customRootDirectory: File? = null

    /**
     * Sets a custom root directory for offline storage.
     *
     * @param directory The custom directory to use, or null to use the default.
     */
    internal fun setCustomRootDirectory(directory: File?) {
        customRootDirectory = directory
    }

    /**
     * The root offline directory: `filesDir/MapTilerSDK/Offline/`
     *
     * @param context Android context
     * @return The root offline directory.
     */
    internal fun getRootDirectory(context: Context): File {
        return customRootDirectory ?: File(context.filesDir, "MapTilerSDK/Offline")
    }

    /**
     * The persistent store for background download mappings.
     *
     * @param context Android context
     * @return The background task mapping file.
     */
    internal fun getBackgroundTaskMappingFile(context: Context): File {
        return File(getRootDirectory(context), "background_tasks.json")
    }

    /**
     * The dedicated temporary directory for offline downloads.
     *
     * @param context Android context
     * @return The temporary directory.
     */
    internal fun getTempDirectory(context: Context): File {
        return File(context.cacheDir, "MTOfflineTemp")
    }

    /**
     * The root directory for a specific offline pack: `getRootDirectory()/<packId>/`
     *
     * @param context Android context
     * @param packId The unique identifier of the pack.
     * @return The pack directory.
     */
    internal fun getPackDirectory(
        context: Context,
        packId: String,
    ): File {
        return File(getRootDirectory(context), packId)
    }

    /**
     * The manifest file path for a specific pack: `<packDirectory>/manifest.json`
     *
     * @param context Android context
     * @param packId The unique identifier of the pack.
     * @return The manifest file.
     */
    internal fun getManifestFile(
        context: Context,
        packId: String,
    ): File {
        return File(getPackDirectory(context, packId), "manifest.json")
    }

    /**
     * The metadata file path for a specific pack: `<packDirectory>/metadata.json`
     *
     * @param context Android context
     * @param packId The unique identifier of the pack.
     * @return The metadata file.
     */
    internal fun getMetadataFile(
        context: Context,
        packId: String,
    ): File {
        return File(getPackDirectory(context, packId), "metadata.json")
    }

    /**
     * The index file path for a specific pack: `<packDirectory>/index.json`
     *
     * @param context Android context
     * @param packId The unique identifier of the pack.
     * @return The index file.
     */
    internal fun getIndexFile(
        context: Context,
        packId: String,
    ): File {
        return File(getPackDirectory(context, packId), "index.json")
    }

    /**
     * The style file path for a specific pack: `<packDirectory>/style.json`
     *
     * @param context Android context
     * @param packId The unique identifier of the pack.
     * @return The style file.
     */
    internal fun getStyleFile(
        context: Context,
        packId: String,
    ): File {
        return File(getPackDirectory(context, packId), "style.json")
    }

    /**
     * The sprite file path for a specific pack.
     *
     * @param context Android context
     * @param packId The unique identifier of the pack.
     * @param scale The pixel ratio of the sprite.
     * @param isJSON Whether the requested file is the JSON manifest or the PNG image.
     * @return The sprite file.
     */
    internal fun getSpriteFile(
        context: Context,
        packId: String,
        scale: Int = 1,
        isJSON: Boolean = false,
    ): File {
        val suffix = if (scale > 1) "@${scale}x" else ""
        val extensionName = if (isJSON) "json" else "png"
        val fileName = "sprite$suffix.$extensionName"
        return File(getPackDirectory(context, packId), fileName)
    }

    /**
     * The glyph path for a specific pack, font stack, and range:
     * `<packDirectory>/glyphs/<fontStack>/<range>.pbf`
     *
     * @param context Android context
     * @param packId The unique identifier of the pack.
     * @param fontStack The font stack name.
     * @param range The glyph range (e.g., "0-255").
     * @return The glyph file.
     */
    internal fun getGlyphsFile(
        context: Context,
        packId: String,
        fontStack: String,
        range: String,
    ): File {
        val glyphsDir = File(getPackDirectory(context, packId), "glyphs")
        val fontStackDir = File(glyphsDir, fontStack)
        return File(fontStackDir, "$range.pbf")
    }

    /**
     * The tile path for a specific pack, source, and tile coordinates:
     * `<packDirectory>/tiles/<sourceId>/<z>/<x>/<y>.pbf`
     *
     * @param context Android context
     * @param packId The unique identifier of the pack.
     * @param sourceId The source identifier.
     * @param z The zoom level.
     * @param x The tile x coordinate.
     * @param y The tile y coordinate.
     * @return The tile file.
     */
    internal fun getTileFile(
        context: Context,
        packId: String,
        sourceId: String,
        z: Int,
        x: Int,
        y: Int,
    ): File {
        val tilesDir = File(getPackDirectory(context, packId), "tiles")
        val sourceDir = File(tilesDir, sourceId)
        val zDir = File(sourceDir, "$z")
        val xDir = File(zDir, "$x")
        return File(xDir, "$y.pbf")
    }

    /**
     * Returns the absolute file for a relative path within a specific pack's directory.
     *
     * @param context Android context
     * @param packId The unique identifier of the pack.
     * @param relativePath The relative path from the pack directory.
     * @return The absolute file.
     */
    internal fun getAbsoluteFile(
        context: Context,
        packId: String,
        relativePath: String,
    ): File {
        return File(getPackDirectory(context, packId), relativePath)
    }
}
