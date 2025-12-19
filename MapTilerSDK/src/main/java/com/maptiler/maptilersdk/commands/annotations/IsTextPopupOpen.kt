/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import com.maptiler.maptilersdk.annotations.MTPopup
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class IsTextPopupOpen(
    val popup: MTPopup,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = true

    override fun toJS(): String = "${popup.identifier}.isOpen();"
}
