/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style

import com.maptiler.maptilersdk.map.style.dsl.Expression
import com.maptiler.maptilersdk.map.style.dsl.MTFeatureKey
import com.maptiler.maptilersdk.map.style.dsl.MTTextToken
import com.maptiler.maptilersdk.map.style.dsl.PropertyValue

/**
 * Typed convenience helpers for common clustering styling patterns.
 */
fun MTStyle.setCircleRadiusStep(
    layerId: String,
    key: MTFeatureKey = MTFeatureKey.POINT_COUNT,
    defaultRadius: Double,
    stops: List<Pair<Double, Double>>,
) {
    val expr =
        Expression.step(
            Expression.get(key),
            PropertyValue.Num(defaultRadius),
            stops.map { (stop, v) -> stop to PropertyValue.Num(v) },
        )
    setPaintProperty(layerId, "circle-radius", expr)
}

suspend fun MTStyle.setCircleRadiusStepAwait(
    layerId: String,
    key: MTFeatureKey = MTFeatureKey.POINT_COUNT,
    defaultRadius: Double,
    stops: List<Pair<Double, Double>>,
) {
    val expr =
        Expression.step(
            Expression.get(key),
            PropertyValue.Num(defaultRadius),
            stops.map { (stop, v) -> stop to PropertyValue.Num(v) },
        )
    setPaintPropertyAwait(layerId, "circle-radius", expr)
}

fun MTStyle.setCircleColorStep(
    layerId: String,
    key: MTFeatureKey = MTFeatureKey.POINT_COUNT,
    defaultColor: Int,
    stops: List<Pair<Double, Int>>,
    includeAlpha: Boolean = false,
) {
    val expr =
        Expression.step(
            Expression.get(key),
            PropertyValue.Color(defaultColor, includeAlpha),
            stops.map { (stop, v) -> stop to PropertyValue.Color(v, includeAlpha) },
        )
    setPaintProperty(layerId, "circle-color", expr)
}

suspend fun MTStyle.setCircleColorStepAwait(
    layerId: String,
    key: MTFeatureKey = MTFeatureKey.POINT_COUNT,
    defaultColor: Int,
    stops: List<Pair<Double, Int>>,
    includeAlpha: Boolean = false,
) {
    val expr =
        Expression.step(
            Expression.get(key),
            PropertyValue.Color(defaultColor, includeAlpha),
            stops.map { (stop, v) -> stop to PropertyValue.Color(v, includeAlpha) },
        )
    setPaintPropertyAwait(layerId, "circle-color", expr)
}

fun MTStyle.setTextFieldToken(
    layerId: String,
    token: MTTextToken,
) = setLayoutProperty(layerId, "text-field", PropertyValue.Str(token.token))

suspend fun MTStyle.setTextFieldTokenAwait(
    layerId: String,
    token: MTTextToken,
) = setLayoutPropertyAwait(layerId, "text-field", PropertyValue.Str(token.token))

fun MTStyle.setTextSize(
    layerId: String,
    size: Double,
) = setLayoutProperty(layerId, "text-size", PropertyValue.Num(size))

suspend fun MTStyle.setTextSizeAwait(
    layerId: String,
    size: Double,
) = setLayoutPropertyAwait(layerId, "text-size", PropertyValue.Num(size))

fun MTStyle.setTextAllowOverlap(
    layerId: String,
    allow: Boolean,
) = setLayoutProperty(layerId, "text-allow-overlap", PropertyValue.Bool(allow))

suspend fun MTStyle.setTextAllowOverlapAwait(
    layerId: String,
    allow: Boolean,
) = setLayoutPropertyAwait(layerId, "text-allow-overlap", PropertyValue.Bool(allow))
