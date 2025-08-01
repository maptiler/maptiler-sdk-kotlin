/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map.workers.stylable

import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.commands.annotations.AddMarker
import com.maptiler.maptilersdk.commands.annotations.RemoveMarker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class StylableWorker(
    private val bridge: MTBridge,
    private val scope: CoroutineScope,
) : MTStylable {
    override fun addMarker(marker: MTMarker) {
        scope.launch {
            bridge.execute(
                AddMarker(marker),
            )
        }
    }

    override fun removeMarker(marker: MTMarker) {
        scope.launch {
            bridge.execute(
                RemoveMarker(marker),
            )
        }
    }
}
