/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.dsl

/**
 * Common GeoJSON feature keys used in clustering.
 */
enum class MTFeatureKey(val key: String) {
    POINT_COUNT("point_count"),
    CLUSTER_ID("cluster_id"),
}
