/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import java.util.Locale

fun Int.toHexString(
    withAlpha: Boolean = true,
    withHexPrefix: Boolean = true,
): String {
    val a = (this ushr 24) and 0xFF
    val r = (this ushr 16) and 0xFF
    val g = (this ushr 8) and 0xFF
    val b = this and 0xFF

    if (withAlpha && a in 1..254) {
        val alphaFloat = a / 255.0f
        return String.format(Locale.US, "rgba(%d, %d, %d, %.3f)", r, g, b, alphaFloat)
    }

    val hex = String.format("#%02X%02X%02X", r, g, b)
    return if (withHexPrefix) hex else hex.substring(1)
}
