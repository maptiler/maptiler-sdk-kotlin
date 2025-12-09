/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import android.graphics.Color
import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.ImageHelper
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.helpers.toHexString

internal data class AddMarker(
    val marker: MTMarker,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        val color =
            if (marker.color != null) {
                marker.color!!.toHexString()
            } else {
                Color.BLUE.toHexString()
            }

        val draggable =
            if (marker.draggable != null) {
                marker.draggable!!
            } else {
                false
            }

        var iconInit = ""
        var iconData = ""
        val popupAttachment =
            if (marker.popup != null) {
                val popup = marker.popup!!

                val offset =
                    if (popup.offset != null) {
                        popup.offset
                    } else {
                        0.0
                    }

                val textJson = JsonConfig.json.encodeToString(popup.text)

                """
                const ${popup.identifier} = new maptilersdk.Popup({ offset: $offset });

                ${popup.identifier}
                .setText($textJson)
                """
            } else {
                ""
            }

        val popupString =
            if (marker.popup != null) {
                "${marker.identifier}.setPopup(${marker.popup!!.identifier})"
            } else {
                ""
            }

        if (marker.icon != null) {
            val encoded = ImageHelper.encodeImageWithMime(marker.icon!!)
            iconInit = """
            var icon${marker.identifier} = new Image();
            icon${marker.identifier}.src = 'data:${encoded.mimeType};base64,${encoded.base64}';
        """

            iconData = "element: icon${marker.identifier}"
        }

        val markerOptions =
            buildString {
                append("color: '$color'")
                append(",\n                draggable: $draggable")
                if (iconData.isNotBlank()) {
                    append(",\n                $iconData")
                }
            }

        return """
            $popupAttachment
            
            $iconInit
            
            const ${marker.identifier} = new maptilersdk.Marker({
                $markerOptions
            });
            
            $popupString

            ${marker.identifier}
            .setLngLat([${marker.coordinates.lng}, ${marker.coordinates.lat}])
            .addTo(${MTBridge.MAP_OBJECT});
            """
    }
}
