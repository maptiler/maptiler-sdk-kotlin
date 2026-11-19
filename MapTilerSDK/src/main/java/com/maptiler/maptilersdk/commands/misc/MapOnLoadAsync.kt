/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.misc

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal class MapOnLoadAsync : MTCommand {
    override val isPrimitiveReturnType: Boolean = false
    override val isPromiseReturnType: Boolean = true

    override fun toJS(): JSString = "${MTBridge.MAP_OBJECT}.onLoadAsync();"
}
