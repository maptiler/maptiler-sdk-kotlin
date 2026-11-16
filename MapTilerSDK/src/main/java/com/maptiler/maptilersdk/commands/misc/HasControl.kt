/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class HasControl(
    val id: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String =
        """
        (function() {
            if (!window.mapControls || !window.mapControls['$id']) return false;
            return ${MTBridge.MAP_OBJECT}.hasControl(window.mapControls['$id']);
        })();
        """.trimIndent()
}
