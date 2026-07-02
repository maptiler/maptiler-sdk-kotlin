/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

/**
 * Inputs required for generating offline coverage.
 */
internal data class MTOfflineCoverageInputs(
    val scheme: String,
    val zoomRange: MTOfflineZoomRange,
)

/**
 * A sequence that lazily generates tile coordinates required to cover a bounding box
 * based on the provided normalized inputs (zoom range, scheme).
 */
internal class MTOfflineCoverageGenerator(
    boundingBox: MTBoundingBox,
    private val inputs: MTOfflineCoverageInputs,
    private val paddingMeters: Double? = null,
) : Sequence<MTTileIndex> {
    private val boundingBoxes = boundingBox.normalizedAndSplit()

    override fun iterator(): Iterator<MTTileIndex> {
        return MTOfflineCoverageIterator(boundingBoxes, inputs, paddingMeters)
    }
}

/**
 * An iterator that generates tiles within the specified bounding box and zoom range.
 */
internal class MTOfflineCoverageIterator(
    private val boundingBoxes: List<MTBoundingBox>,
    private val inputs: MTOfflineCoverageInputs,
    private val paddingMeters: Double?,
) : Iterator<MTTileIndex> {
    private var currentBoxIndex: Int = 0
    private var currentZoom: Int
    private var boundsX: IntRange
    private var boundsY: IntRange

    private var currentX: Int
    private var currentY: Int
    private var isCompleted: Boolean = false

    init {
        currentZoom = inputs.zoomRange.minZoom

        if (boundingBoxes.isEmpty() || inputs.zoomRange.minZoom > inputs.zoomRange.maxZoom) {
            isCompleted = true
            boundsX = 0..0
            boundsY = 0..0
            currentX = 0
            currentY = 0
        } else {
            val bounds = MTTileMath.tileRanges(boundingBoxes[0], currentZoom, paddingMeters)
            boundsX = bounds.first
            boundsY = bounds.second
            currentX = boundsX.first
            currentY = boundsY.first
        }
    }

    override fun hasNext(): Boolean {
        return !isCompleted
    }

    override fun next(): MTTileIndex {
        if (isCompleted) throw NoSuchElementException()

        // Transform the Y coordinate if the target scheme requires it.
        val effectiveY =
            if (inputs.scheme == "tms") {
                MTTileMath.flipYCoordinate(currentY, currentZoom)
            } else {
                currentY
            }
        val tile = MTTileIndex(currentX, effectiveY, currentZoom)

        // Advance to the next coordinate
        currentX++
        if (currentX > boundsX.last) {
            currentX = boundsX.first
            currentY++

            if (currentY > boundsY.last) {
                currentZoom++
                if (currentZoom > inputs.zoomRange.maxZoom) {
                    currentBoxIndex++
                    if (currentBoxIndex < boundingBoxes.size) {
                        currentZoom = inputs.zoomRange.minZoom
                        val bounds = MTTileMath.tileRanges(boundingBoxes[currentBoxIndex], currentZoom, paddingMeters)
                        boundsX = bounds.first
                        boundsY = bounds.second
                        currentX = boundsX.first
                        currentY = boundsY.first
                    } else {
                        isCompleted = true
                    }
                } else {
                    val bounds = MTTileMath.tileRanges(boundingBoxes[currentBoxIndex], currentZoom, paddingMeters)
                    boundsX = bounds.first
                    boundsY = bounds.second
                    currentX = boundsX.first
                    currentY = boundsY.first
                }
            }
        }

        return tile
    }
}
