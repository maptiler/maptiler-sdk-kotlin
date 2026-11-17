/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal class GetLayersOrder : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String = "JSON.stringify(${MTBridge.MAP_OBJECT}.getLayersOrder());"
}
