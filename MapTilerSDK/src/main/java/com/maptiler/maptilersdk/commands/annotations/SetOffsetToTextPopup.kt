/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

internal data class SetOffsetToTextPopup(
    val popup: MTTextPopup,
    val offset: Double?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val offsetJson = JsonConfig.json.encodeToString(Double.serializer().nullable, offset)

        return "${popup.identifier}.setOffset($offsetJson);"
    }
}
