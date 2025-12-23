/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import com.maptiler.maptilersdk.annotations.MTTextPopup
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.JsonConfig

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
        val subpixelPositioning = popup.subpixelPositioning?.let { ", subpixelPositioning: $it" } ?: ""

        val maxWidth = popup.maxWidth?.let { JsonConfig.json.encodeToString("${it}px") }
        val textJson = JsonConfig.json.encodeToString(popup.text)

        val setMaxWidth =
            if (maxWidth != null) {
                ".setMaxWidth($maxWidth)\n            "
            } else {
                ""
            }

        val popupOptions = "{ offset: $offset$subpixelPositioning }"

        val js = """
            const ${popup.identifier} = new maptilersdk.Popup($popupOptions);

            // Attach open/close event forwarding to Android bridge
            ${popup.identifier}.on('open', () => {
                const lngLat = ${popup.identifier}.getLngLat();
                const data = {
                    id: '${popup.identifier}',
                    lngLat: {
                        lng: lngLat.lng,
                        lat: lngLat.lat
                    }
                };
                Android.onEvent("popup.open", JSON.stringify(data));
            });

            ${popup.identifier}.on('close', () => {
                const lngLat = ${popup.identifier}.getLngLat();
                const data = {
                    id: '${popup.identifier}',
                    lngLat: {
                        lng: lngLat.lng,
                        lat: lngLat.lat
                    }
                };
                Android.onEvent("popup.close", JSON.stringify(data));
            });

            ${popup.identifier}
            $setMaxWidth.setLngLat([${popup.coordinates.lng}, ${popup.coordinates.lat}])
            .setText($textJson)
            .addTo(${MTBridge.MAP_OBJECT});
            """
        return js
    }
}
