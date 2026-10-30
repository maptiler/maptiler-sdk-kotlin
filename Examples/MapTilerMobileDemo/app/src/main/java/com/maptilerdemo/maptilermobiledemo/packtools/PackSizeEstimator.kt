/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptilerdemo.maptilermobiledemo.packtools

import com.maptiler.maptilersdk.offline.MTOfflineEstimator
import com.maptiler.maptilersdk.offline.MTOfflineRegionDefinition
import com.maptiler.maptilersdk.offline.MTOfflineStyleMetadata
import com.maptiler.maptilersdk.offline.MTPackStats
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Base64
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.pow

// Pack size prediction from the calibrated size model (maptiler-offline-estimator/calibration). The iOS demo app
// and the web estimator implement the same algorithm; size-estimate-fixtures.json keeps them in agreement.

val packToolsJson = Json { ignoreUnknownKeys = true }

/** Measured byte counts of one resource or a group of resources. */
@Serializable
data class PackBytes(
    /** Bytes stored on disk (and transferred: MapTiler serves tiles and glyphs uncompressed). */
    val bytes: Double = 0.0,
    val wire: Double = 0.0,
    /** Bytes allocated on disk, rounding every file up to whole file system blocks. */
    val allocated: Double = 0.0,
) {
    operator fun plus(other: PackBytes) = PackBytes(bytes + other.bytes, wire + other.wire, allocated + other.allocated)

    operator fun times(factor: Double) = PackBytes(bytes * factor, wire * factor, allocated * factor)
}

/** Style metadata of every built-in style, keyed like "streets/default". */
@Serializable
data class PackSnapshot(
    val version: Int,
    val generatedAt: String,
    val styles: Map<String, MTOfflineStyleMetadata>,
)

@Serializable
data class PackSizeModel(
    val version: Int,
    val generatedAt: String,
    val groupZoom: Int,
    val blockSize: Int,
    val tilesets: Map<String, Tileset>,
    val overhead: Overhead,
    val packFiles: PackFiles? = null,
    val population: Population? = null,
) {
    @Serializable
    data class ExactZoom(val bytes: String, val wire: String)

    @Serializable
    data class Accuracy(val medianError: Double, val p90Error: Double, val sites: Int)

    @Serializable
    data class Tileset(
        val maxCalibratedZoom: Int,
        val exact: Map<String, ExactZoom>,
        val densityTileset: String? = null,
        val classBounds: List<Double>,
        val scales: Map<String, Map<String, PackBytes>>,
        val populationFactors: Map<String, PackBytes>? = null,
        val accuracy: Accuracy? = null,
    )

    @Serializable
    data class PackFiles(val fixedBytes: Double, val bytesPerResource: Double)

    @Serializable
    data class Glyphs(
        val reference: String,
        val referenceRanges: Map<String, PackBytes>,
        val fontStacks: Map<String, FontStack>,
    ) {
        @Serializable
        data class FontStack(val factor: Double, val ranges: Map<String, PackBytes>)
    }

    @Serializable
    data class Overhead(
        val styles: Map<String, PackBytes>,
        val sprites: Map<String, PackBytes>,
        val glyphs: Glyphs? = null,
    )

    @Serializable
    data class Population(val zoom: Int, val file: String, val bins: List<Double>)
}

data class PackSizeEstimate(
    val tiles: PackBytes,
    val overhead: PackBytes,
    val packFiles: PackBytes,
    /** Relative error (90th percentile from calibration), weighted by tile bytes. */
    val tileError: Double,
    val uncalibratedSources: List<String>,
    val extrapolatedSources: List<String>,
    val unmeasuredOverhead: List<String>,
) {
    val total: PackBytes get() = tiles + overhead + packFiles

    /** Lower bound, value and upper bound of one channel. */
    fun range(channel: (PackBytes) -> Double): Triple<Double, Double, Double> {
        val value = channel(total)
        val margin = channel(tiles) * tileError
        return Triple(max(0.0, value - margin), value, value + margin)
    }
}

/** URL path without scheme, host and query: how the model keys tilesets and resources. */
fun pathKey(url: String): String {
    var rest = url.substringBefore("?")
    val scheme = rest.indexOf("://")
    if (scheme >= 0) {
        rest = rest.substring(scheme + 3)
        val slash = rest.indexOf("/")
        rest = if (slash >= 0) rest.substring(slash) else ""
    }
    return rest
}

/** Population per tile at the grid's zoom, with sums for coarser tiles. */
class PopulationGrid(val zoom: Int, private val values: IntArray) {
    private val sums = HashMap<String, Double>()

    /** Population of a tile at [tileZoom] (not deeper than the grid), scaled to the area of a grid tile. */
    fun density(tileZoom: Int, x: Int, y: Int): Double =
        sums.getOrPut("$tileZoom/$x/$y") {
            val shift = zoom - tileZoom
            val size = 1 shl shift
            val width = 1 shl zoom
            var total = 0.0
            for (row in (y shl shift) until (y shl shift) + size) {
                for (column in (x shl shift) until (x shl shift) + size) {
                    total += (values[row * width + column].toLong() and 0xFFFFFFFFL).toDouble()
                }
            }
            total / 4.0.pow(shift)
        }

    companion object {
        fun fromBytes(zoom: Int, bytes: ByteArray): PopulationGrid? {
            if (bytes.size != 4 shl (2 * zoom)) return null
            val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer()
            return PopulationGrid(zoom, IntArray(buffer.remaining()).also { buffer.get(it) })
        }
    }
}

object PackSizeEstimator {
    // Rough fallbacks matching the SDK's averages, used only where the model has no data.
    private const val FALLBACK_VECTOR_TILE = 25_000.0
    private const val FALLBACK_RASTER_TILE = 150_000.0
    private const val FALLBACK_STYLE = 100_000.0
    private const val FALLBACK_SPRITE_FILE = 50_000.0
    private const val FALLBACK_GLYPH_RANGE = 15_000.0

    private fun rough(bytes: Double) = PackBytes(bytes, bytes, bytes)

    fun estimate(
        region: MTOfflineRegionDefinition,
        metadata: MTOfflineStyleMetadata,
        stats: MTPackStats,
        model: PackSizeModel,
        population: PopulationGrid?,
    ): PackSizeEstimate {
        val exact = ExactSizes(model)
        val groupZoom = model.groupZoom
        val block = model.blockSize.toDouble()
        fun roundUp(bytes: Double) = ceil(bytes / block) * block

        val sources = (metadata.sources + listOfNotNull(metadata.terrainSource)).associateBy { it.id }
        var tiles = PackBytes()
        var errorWeighted = 0.0
        val uncalibrated = mutableListOf<String>()
        val extrapolated = mutableListOf<String>()

        // With population data, tiles are grouped finer so each group has its own density.
        val groups = MTOfflineEstimator().tileGroups(region, metadata, maxOf(groupZoom, population?.zoom ?: groupZoom))
        for (group in groups) {
            val source = sources[group.sourceId] ?: continue
            val key = pathKey(source.tileUrlTemplate)
            val tileset = model.tilesets[key]
            if (tileset == null) {
                val isRaster = source.type?.contains("raster") == true
                tiles += rough(group.tileCount * if (isRaster) FALLBACK_RASTER_TILE else FALLBACK_VECTOR_TILE)
                if (source.id !in uncalibrated) uncalibrated += source.id
                continue
            }

            val ancestorZoom = minOf(group.ancestorZoom, groupZoom)
            val shift = group.ancestorZoom - ancestorZoom
            val ancestor = exact.size(tileset.densityTileset ?: key, ancestorZoom, group.ancestorX shr shift, group.ancestorY shr shift)

            val predicted =
                if (group.zoom <= groupZoom) {
                    PackBytes(ancestor.first, ancestor.second, roundUp(ancestor.first))
                } else {
                    val densityClass = tileset.classBounds.count { it <= ancestor.first }
                    val level = group.zoom.toString()
                    val scale = tileset.scales[densityClass.toString()]?.get(level) ?: tileset.scales["all"]?.get(level) ?: PackBytes()
                    var factor = PackBytes(1.0, 1.0, 1.0)
                    val factors = tileset.populationFactors
                    val bins = model.population?.bins
                    if (population != null && factors != null && bins != null) {
                        val density = population.density(group.ancestorZoom, group.ancestorX, group.ancestorY)
                        factor = factors[bins.count { it <= density }.toString()] ?: factor
                    }
                    if (group.zoom > tileset.maxCalibratedZoom && source.id !in extrapolated) extrapolated += source.id
                    PackBytes(
                        ancestor.first * scale.bytes * factor.bytes,
                        ancestor.second * scale.wire * factor.wire,
                        roundUp(ancestor.first) * scale.allocated * factor.allocated,
                    ) * group.tileCount.toDouble()
                }
            tiles += predicted
            errorWeighted += predicted.bytes * (tileset.accuracy?.p90Error ?: 1.0)
        }

        var overhead = PackBytes()
        val unmeasured = mutableListOf<String>()
        metadata.styleUrl?.let { styleUrl ->
            val measured = model.overhead.styles[pathKey(styleUrl)]
            overhead += measured ?: rough(FALLBACK_STYLE)
            if (measured == null) unmeasured += "style"
        }
        for (sprite in metadata.sprites) {
            val measured = model.overhead.sprites[pathKey(sprite.url)]
            overhead += measured ?: rough(FALLBACK_SPRITE_FILE * 4)
            if (measured == null) unmeasured += "sprite ${sprite.id}"
        }
        if (metadata.glyphsTemplate != null) {
            val starts = region.glyphRanges.rangeStarts.map { it.toString() }
            val glyphs = model.overhead.glyphs
            for (fontStack in metadata.fontStacks) {
                val key = fontStack.joinToString(",")
                val stack = glyphs?.fontStacks?.get(key)
                if (glyphs == null || stack == null) {
                    overhead += rough(FALLBACK_GLYPH_RANGE * starts.size)
                    unmeasured += "glyphs $key"
                    continue
                }
                for (start in starts) {
                    val measured = stack.ranges[start] ?: glyphs.referenceRanges[start]?.times(stack.factor)
                    overhead += measured ?: rough(FALLBACK_GLYPH_RANGE)
                }
            }
        }

        val packFiles =
            model.packFiles?.let {
                val bytes = it.fixedBytes + it.bytesPerResource * stats.resourceCount
                PackBytes(bytes, 0.0, roundUp(bytes))
            } ?: PackBytes()

        return PackSizeEstimate(
            tiles = tiles,
            overhead = overhead,
            packFiles = packFiles,
            tileError = if (tiles.bytes > 0) errorWeighted / tiles.bytes else 0.0,
            uncalibratedSources = uncalibrated,
            extrapolatedSources = extrapolated,
            unmeasuredOverhead = unmeasured,
        )
    }

    /** Decodes and caches the per-tile sizes stored in the model. */
    private class ExactSizes(private val model: PackSizeModel) {
        private val cache = HashMap<String, Pair<IntArray, IntArray>>()

        fun size(tileset: String, zoom: Int, x: Int, y: Int): Pair<Double, Double> {
            val values =
                cache.getOrPut("$tileset|$zoom") {
                    val exact = model.tilesets[tileset]?.exact?.get(zoom.toString()) ?: return 0.0 to 0.0
                    decode(exact.bytes) to decode(exact.wire)
                }
            val index = y * (1 shl zoom) + x
            if (index < 0 || index >= values.first.size || index >= values.second.size) return 0.0 to 0.0
            return unsigned(values.first[index]) to unsigned(values.second[index])
        }

        private fun unsigned(value: Int) = (value.toLong() and 0xFFFFFFFFL).toDouble()

        private fun decode(base64: String): IntArray {
            val buffer = ByteBuffer.wrap(Base64.getDecoder().decode(base64)).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer()
            return IntArray(buffer.remaining()).also { buffer.get(it) }
        }
    }
}
