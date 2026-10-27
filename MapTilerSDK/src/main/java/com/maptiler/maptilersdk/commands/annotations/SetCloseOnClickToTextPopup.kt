/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import kotlinx.serialization.builtins.serializer

internal data class SetCloseOnClickToTextPopup(
    val popup: MTTextPopup,
    val closeOnClick: Boolean,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val closeOnClickJson = JsonConfig.json.encodeToString(Boolean.serializer(), closeOnClick)

        return "${popup.identifier}.setCloseOnClick($closeOnClickJson);"
    }
}
