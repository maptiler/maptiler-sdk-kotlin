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

        val anchor = marker.anchor.value
        val pitchAlignment = marker.pitchAlignment.value
        val rotation = marker.rotation
        val scale = marker.scale
        val rotationAlignment = marker.rotationAlignment.value
        val offset = marker.offset
        val opacity = marker.opacity
        val opacityWhenCovered = marker.opacityWhenCovered
        val subpixelPositioning = marker.subpixelPositioning

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
                val maxWidth = popup.maxWidth?.let { JsonConfig.json.encodeToString(it) }
                val setMaxWidth =
                    if (maxWidth != null) {
                        ".setMaxWidth($maxWidth)\n                "
                    } else {
                        ""
                    }

                """
                const ${popup.identifier} = new maptilersdk.Popup({ offset: $offset });

                ${popup.identifier}
                $setMaxWidth.setText($textJson)
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
                append(",\n                scale: $scale")
                append(",\n                draggable: $draggable")
                append(",\n                anchor: '$anchor'")
                append(",\n                pitchAlignment: '$pitchAlignment'")
                append(",\n                rotation: $rotation")
                append(",\n                rotationAlignment: '$rotationAlignment'")
                append(",\n                offset: $offset")
                append(",\n                opacity: $opacity")
                append(",\n                opacityWhenCovered: $opacityWhenCovered")
                append(",\n                subpixelPositioning: $subpixelPositioning")
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

            const handle${marker.identifier}DragEvent = (eventName) => (event) => {
                const lngLat = ${marker.identifier}.getLngLat();
                const point = event && event.point ? { x: event.point.x, y: event.point.y } : null;
                const data = {
                    id: '${marker.identifier}',
                    lngLat: {
                        lng: lngLat.lng,
                        lat: lngLat.lat
                    },
                    point
                };

                Android.onEvent(eventName, JSON.stringify(data));
            };

            ${marker.identifier}.on('drag', handle${marker.identifier}DragEvent('marker.drag'));
            ${marker.identifier}.on('dragend', handle${marker.identifier}DragEvent('marker.dragend'));
            ${marker.identifier}.on('dragstart', handle${marker.identifier}DragEvent('marker.dragstart'));
            """
    }
}
