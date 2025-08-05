/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Base64
import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.toHexString
import java.io.ByteArrayOutputStream

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

                """
                const ${popup.identifier} = new maptilersdk.Popup({ offset: $offset });

                ${popup.identifier}
                .setText('${popup.text}')
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
            iconInit = """
            var icon${marker.identifier} = new Image();
            icon${marker.identifier}.src = 'data:image/png;base64,${encodeImage(marker.icon!!)}';
        """

            iconData = "element: icon${marker.identifier}"
        }

        return """
            $popupAttachment
            
            $iconInit
            
            const ${marker.identifier} = new maptilersdk.Marker({
                color: '$color',
                draggable: $draggable,
                $iconData
            });
            
            $popupString

            ${marker.identifier}
            .setLngLat([${marker.coordinates.lng}, ${marker.coordinates.lat}])
            .addTo(${MTBridge.MAP_OBJECT});
            """
    }

    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()

        val compressFormat =
            if (bm.hasAlpha()) {
                Bitmap.CompressFormat.PNG
            } else {
                Bitmap.CompressFormat.JPEG
            }

        bm.compress(compressFormat, 100, baos)
        val byteArray = baos.toByteArray()

        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
        val finalString =
            base64String
                .replace("\\", "\\\\'")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")

        return finalString
    }
}
