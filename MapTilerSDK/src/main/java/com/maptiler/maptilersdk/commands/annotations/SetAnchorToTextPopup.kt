/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import com.maptiler.maptilersdk.annotations.MTAnchor
import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

internal data class SetAnchorToTextPopup(
    val popup: MTTextPopup,
    val anchor: MTAnchor?,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val anchorJson = JsonConfig.json.encodeToString(String.serializer().nullable, anchor?.value)

        return "${popup.identifier}.setAnchor($anchorJson);"
    }
}
