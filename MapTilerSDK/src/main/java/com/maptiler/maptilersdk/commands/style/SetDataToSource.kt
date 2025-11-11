/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.formatUrlForJs
import com.maptiler.maptilersdk.map.style.source.MTSource
import java.net.URL

internal data class SetDataToSource(
    val data: URL,
    val source: MTSource,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String = "${MTBridge.MAP_OBJECT}.getSource('${source.identifier}').setData('${formatUrlForJs(data)}');"
}
