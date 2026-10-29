/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo.packtools

import android.content.Context
import android.system.Os
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import java.io.File

// Hidden pack estimator tools. The data in assets/packtools comes from maptiler-offline-estimator/calibration
// (snapshot.json, size-model.json, population-z10.u32).

/** A built-in style and variant, identified by a stable key such as "streets/dark". */
data class PackStyle(val style: MTMapReferenceStyle, val variant: MTMapStyleVariant) {
    val id: String get() = "${style.getName().lowercase()}/${variant.value}"

    companion object {
        private val styles =
            listOf(
                MTMapReferenceStyle.STREETS, MTMapReferenceStyle.OUTDOOR, MTMapReferenceStyle.SATELLITE,
                MTMapReferenceStyle.WINTER, MTMapReferenceStyle.DATAVIZ, MTMapReferenceStyle.BASE,
                MTMapReferenceStyle.BRIGHT, MTMapReferenceStyle.TOPO, MTMapReferenceStyle.TONER,
                MTMapReferenceStyle.BACKDROP, MTMapReferenceStyle.OPENSTREETMAP, MTMapReferenceStyle.AQUARELLE,
                MTMapReferenceStyle.LANDSCAPE, MTMapReferenceStyle.OCEAN,
            )

        val all: List<PackStyle> =
            styles.flatMap { style ->
                (style.getVariants() ?: listOf(MTMapStyleVariant.DEFAULT_VARIANT)).map { PackStyle(style, it) }
            }

        fun withId(id: String): PackStyle? = all.firstOrNull { it.id == id }
    }
}

/** The calibrated data bundled with the app. */
class PackToolsData(
    val snapshot: PackSnapshot,
    val model: PackSizeModel?,
    val population: PopulationGrid?,
) {
    companion object {
        fun load(context: Context): PackToolsData {
            val assets = context.assets
            fun read(name: String) = assets.open("packtools/$name").use { it.readBytes() }
            val snapshot = packToolsJson.decodeFromString<PackSnapshot>(read("snapshot.json").decodeToString())
            val model = runCatching { packToolsJson.decodeFromString<PackSizeModel>(read("size-model.json").decodeToString()) }.getOrNull()
            val population = model?.population?.let { runCatching { PopulationGrid.fromBytes(it.zoom, read(it.file)) }.getOrNull() }
            return PackToolsData(snapshot, model, population)
        }
    }
}

/** Bytes of the files a pack wrote, grouped like the size estimate. */
data class PackMeasurement(
    val tiles: PackBytes,
    val overhead: PackBytes,
    val packFiles: PackBytes,
    val tileFiles: Int,
) {
    val total: PackBytes get() = tiles + overhead + packFiles

    companion object {
        /** Measures a pack directory (filesDir/MapTilerSDK/Offline/<pack id>). */
        fun measure(context: Context, packId: String): PackMeasurement {
            val directory = File(context.filesDir, "MapTilerSDK/Offline/$packId")
            var tiles = PackBytes()
            var overhead = PackBytes()
            var packFiles = PackBytes()
            var tileFiles = 0
            directory.walkTopDown().filter { it.isFile }.forEach { file ->
                val bytes = file.length().toDouble()
                // st_blocks is in 512-byte units.
                val allocated = runCatching { Os.stat(file.path).st_blocks * 512.0 }.getOrDefault(bytes)
                val measured = PackBytes(bytes, bytes, allocated)
                val relative = file.relativeTo(directory).invariantSeparatorsPath
                when {
                    relative.startsWith("tiles/") -> {
                        tiles += measured
                        tileFiles++
                    }
                    relative.startsWith("glyphs/") || relative.startsWith("sprite") || relative == "style.json" -> overhead += measured
                    else -> packFiles += measured
                }
            }
            return PackMeasurement(tiles, overhead, packFiles, tileFiles)
        }
    }
}
