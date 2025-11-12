/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.navigation

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal class GetMaxPitch : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String = "${MTBridge.MAP_OBJECT}.getMaxPitch();"
}
