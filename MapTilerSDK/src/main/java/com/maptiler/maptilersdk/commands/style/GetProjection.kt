/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

/**
 * Returns the current projection type as a string: "mercator" | "globe".
 * Returns null if no projection is set.
 */
internal class GetProjection : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    // Use fallback object to avoid optional chaining and keep compatibility.
    // If getProjection() returns undefined, this yields undefined which Android bridges as null.
    override fun toJS(): String = "(${MTBridge.MAP_OBJECT}.getProjection() || {}).type;"
}
