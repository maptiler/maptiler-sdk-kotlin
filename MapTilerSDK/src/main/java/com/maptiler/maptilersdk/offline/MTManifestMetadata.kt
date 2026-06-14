/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.offline

import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle
import com.maptiler.maptilersdk.map.style.MTMapStyleVariant
import kotlinx.serialization.Serializable

/**
 * Metadata storing the original request parameters for the manifest.
 */
@Serializable
internal data class MTManifestMetadata(
    @Serializable(with = MTMapReferenceStyleSerializer::class)
    val referenceStyle: MTMapReferenceStyle,
    @Serializable(with = MTMapStyleVariantSerializer::class)
    val styleVariant: MTMapStyleVariant? = null,
    val bbox: MTBoundingBox,
    val minZoom: Int,
    val maxZoom: Int,
    val pixelRatio: Float,
)
