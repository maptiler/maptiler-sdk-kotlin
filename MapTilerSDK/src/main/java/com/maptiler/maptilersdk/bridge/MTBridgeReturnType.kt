/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.bridge

internal sealed class MTBridgeReturnType {
    data class StringValue(val value: String) : MTBridgeReturnType()

    data class DoubleValue(val value: Double) : MTBridgeReturnType()

    data class BoolValue(val value: Boolean) : MTBridgeReturnType()

    data class StringDoubleDict(val value: Map<String, Double>) : MTBridgeReturnType()

    data object UnsupportedType : MTBridgeReturnType()

    data object Null : MTBridgeReturnType()

    companion object {
        @Throws(MTError.InvalidResultType::class)
        fun from(value: Any?): MTBridgeReturnType {
            return when (value) {
                is String -> StringValue(value)
                is Double -> DoubleValue(value)
                is Boolean -> BoolValue(value)
                is Map<*, *> -> {
                    val isValidMap =
                        value.keys.all { it is String } &&
                            value.values.all { it is Double }
                    if (isValidMap) {
                        @Suppress("UNCHECKED_CAST")
                        StringDoubleDict(value as Map<String, Double>)
                    } else {
                        throw MTError.InvalidResultType(description = value.toString())
                    }
                }
                null -> Null
                else -> throw MTError.InvalidResultType(description = value.toString())
            }
        }
    }
}
