/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.bridge

internal typealias JSString = String

internal interface MTCommand {
    fun toJS(): JSString
}

internal interface MTCommandExecutable {
    suspend fun execute(command: MTCommand): MTBridgeReturnType
}
