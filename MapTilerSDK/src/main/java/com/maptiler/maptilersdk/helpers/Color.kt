/**
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 *
 * @author sasaprodribaba
 * Created 30. 5. 2025. at 12:02
 */

package com.maptiler.maptilersdk.helpers

fun Int.toHexString(
    withAlpha: Boolean = false,
    withHexPrefix: Boolean = true,
): String {
    val hex =
        if (withAlpha) {
            String.format("#%08X", this)
        } else {
            String.format("#%06X", 0xFFFFFF and this)
        }

    return if (withHexPrefix) hex else hex.substring(1)
}
