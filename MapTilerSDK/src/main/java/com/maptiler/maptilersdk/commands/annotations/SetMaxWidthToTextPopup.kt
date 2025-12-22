/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig

internal data class SetMaxWidthToTextPopup(
    val popup: MTTextPopup,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val maxWidth = popup.maxWidth ?: ""
        val maxWidthJson = JsonConfig.json.encodeToString(maxWidth)

        return "${popup.identifier}.setMaxWidth($maxWidthJson);"
    }
}
