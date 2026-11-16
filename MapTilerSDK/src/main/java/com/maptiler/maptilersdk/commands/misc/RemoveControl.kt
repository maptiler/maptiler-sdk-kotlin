/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class RemoveControl(
    val id: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String =
        """
        if (window.mapControls && window.mapControls['$id']) {
            ${MTBridge.MAP_OBJECT}.removeControl(window.mapControls['$id']);
            delete window.mapControls['$id'];
        }
        """.trimIndent()
}
