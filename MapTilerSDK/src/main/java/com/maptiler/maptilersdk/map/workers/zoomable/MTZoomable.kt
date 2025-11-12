/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.zoomable

/**
 * Defines methods for manipulating zoom level.
 */
interface MTZoomable {
    /**
     * Increases the map's zoom level by 1.
     */
    fun zoomIn()

    /**
     * Decreases the map's zoom level by 1.
     */
    fun zoomOut()

    /**
     * Returns the map's current zoom level.
     */
    suspend fun getZoom(): Double

    /**
     * Returns the map's maximum zoom level.
     */
    suspend fun getMaxZoom(): Double

    /**
     * Returns the map's minimum zoom level.
     */
    suspend fun getMinZoom(): Double

    /**
     * Sets the map's zoom level.
     *
     * @param zoom The zoom level to set (0-20).
     */
    fun setZoom(zoom: Double)

    /**
     * Sets the map's maximum zoom level.
     *
     * @param maxZoom The max zoom level to set (0-20).
     */
    fun setMaxZoom(maxZoom: Double)

    /**
     * Sets the map's minimum zoom level.
     *
     * @param minZoom The min zoom level to set (0-20).
     */
    fun setMinZoom(minZoom: Double)
}
