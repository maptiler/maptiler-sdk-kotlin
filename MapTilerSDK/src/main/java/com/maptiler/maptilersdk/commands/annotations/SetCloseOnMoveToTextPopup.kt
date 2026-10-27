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

internal data class SetCloseOnMoveToTextPopup(
    val popup: MTTextPopup,
    val closeOnMove: Boolean,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val closeOnMoveJson = JsonConfig.json.encodeToString(Boolean.serializer(), closeOnMove)

        return "${popup.identifier}.setCloseOnMove($closeOnMoveJson);"
    }
}
