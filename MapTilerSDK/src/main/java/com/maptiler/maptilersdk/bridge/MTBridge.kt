/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.bridge

/**
 * Class responsible for bridging external implementations and Kotlin code
 *
 * It uses abstract executor as mediator object allowing outside executor implementations
 */
internal sealed class MTBridge(
    private val executor: MTCommandExecutable?,
) {
    companion object {
        const val MAP_OBJECT: JSString = "map"
        const val SDK_OBJECT: JSString = "maptilersdk"
        const val STYLE_OBJECT: JSString = "MapStyle"
    }

    suspend fun execute(command: MTCommand): MTBridgeReturnType? = executor?.execute(command)
}
