/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.bridge

typealias JSString = String

/**
 * Protocol for all types of commands.
 */
interface MTCommand {
    val isPrimitiveReturnType: Boolean

    fun toJS(): JSString
}

interface MTCommandExecutable {
    suspend fun execute(command: MTCommand): MTBridgeReturnType
}
