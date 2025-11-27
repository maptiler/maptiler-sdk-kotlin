/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.style.dsl

import com.maptiler.maptilersdk.helpers.toHexString

/**
 * Type-safe representation of style property values.
 */
sealed class PropertyValue {
    data class Str(val value: String) : PropertyValue()

    data class Num(val value: Double) : PropertyValue()

    data class Bool(val value: Boolean) : PropertyValue()

    data class Arr(val values: List<PropertyValue>) : PropertyValue()

    data class RawJs(val value: String) : PropertyValue()

    data class Color(val argb: Int, val includeAlpha: Boolean = false) : PropertyValue()

    fun asCode(): String =
        when (this) {
            is Str -> jsonString(value)
            is Num ->
                if (value.isFinite()) value.toString() else "0.0"
            is Bool -> if (value) "true" else "false"
            is Arr -> values.joinToString(prefix = "[", postfix = "]") { it.asCode() }
            is RawJs -> value
            is Color -> jsonString(argb.toHexString(includeAlpha))
        }

    companion object {
        fun of(value: String): PropertyValue = Str(value)

        fun of(value: Double): PropertyValue = Num(value)

        fun of(value: Int): PropertyValue = Num(value.toDouble())

        fun of(value: Boolean): PropertyValue = Bool(value)

        fun color(
            value: Int,
            includeAlpha: Boolean = false,
        ): PropertyValue = Color(value, includeAlpha)

        fun array(vararg values: PropertyValue): PropertyValue = Arr(values.toList())
    }
}

private fun jsonString(s: String): String =
    buildString {
        append('"')
        s.forEach { ch ->
            when (ch) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(ch)
            }
        }
        append('"')
    }
