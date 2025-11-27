/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.dsl

/**
 * Simple typed expression helpers that encode to JSON arrays.
 */
object Expression {
    fun get(key: MTFeatureKey): PropertyValue = PropertyValue.Arr(listOf(PropertyValue.Str("get"), PropertyValue.Str(key.key)))

    fun toString(value: PropertyValue): PropertyValue = PropertyValue.Arr(listOf(PropertyValue.Str("to-string"), value))

    fun step(
        input: PropertyValue,
        default: PropertyValue,
        stops: List<Pair<Double, PropertyValue>>,
    ): PropertyValue {
        val items = mutableListOf<PropertyValue>(PropertyValue.Str("step"), input, default)
        stops.forEach { (stop, v) ->
            items.add(PropertyValue.Num(stop))
            items.add(v)
        }
        return PropertyValue.Arr(items)
    }
}
