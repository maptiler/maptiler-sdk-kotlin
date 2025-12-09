/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.dsl

/**
 * Filter helpers that build GL-style filter arrays.
 */
object MTFilter {
    fun has(key: MTFeatureKey): PropertyValue = PropertyValue.Arr(listOf(PropertyValue.Str("has"), PropertyValue.Str(key.key)))

    fun not(filter: PropertyValue): PropertyValue = PropertyValue.Arr(listOf(PropertyValue.Str("!"), filter))

    fun eq(
        key: MTFeatureKey,
        value: PropertyValue,
    ): PropertyValue = PropertyValue.Arr(listOf(PropertyValue.Str("=="), MTExpression.get(key), value))

    fun all(vararg filters: PropertyValue): PropertyValue = PropertyValue.Arr(listOf(PropertyValue.Str("all")) + filters)

    fun any(vararg filters: PropertyValue): PropertyValue = PropertyValue.Arr(listOf(PropertyValue.Str("any")) + filters)

    fun clusters(): PropertyValue = has(MTFeatureKey.POINT_COUNT)

    fun unclustered(): PropertyValue = not(has(MTFeatureKey.POINT_COUNT))
}
