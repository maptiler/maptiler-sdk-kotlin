/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand

internal data class AddTextPopup(
    val popup: MTTextPopup,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val offset =
            if (popup.offset != null) {
                popup.offset
            } else {
                0.0
            }

        return """
            const ${popup.identifier} = new maptilersdk.Popup({ offset: $offset });

            ${popup.identifier}
            .setLngLat([${popup.coordinates.lng}, ${popup.coordinates.lat}])
            .setText('${popup.text}')
            .addTo(${MTBridge.MAP_OBJECT});
            """
    }
}
