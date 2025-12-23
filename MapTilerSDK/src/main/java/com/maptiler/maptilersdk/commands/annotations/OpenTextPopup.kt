/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class OpenTextPopup(
    val popup: MTTextPopup,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${popup.identifier}.addTo(${MTBridge.MAP_OBJECT});"
}
