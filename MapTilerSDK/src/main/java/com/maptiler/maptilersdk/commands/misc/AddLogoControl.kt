/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class AddLogoControl(
    val id: String,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String =
        """
        if (!window.mapControls) window.mapControls = {};
        window.mapControls['$id'] = new ${MTBridge.SDK_OBJECT}.MaptilerLogoControl();
        ${MTBridge.MAP_OBJECT}.addControl(window.mapControls['$id']);
        """.trimIndent()
}
