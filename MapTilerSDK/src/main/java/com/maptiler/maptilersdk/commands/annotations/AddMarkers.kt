/*
 * Copyright (c) 2026, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import android.graphics.Bitmap
import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.ImageHelper

internal data class AddMarkers(
    val markers: List<MTMarker>,
    val withSingleIcon: Bitmap? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String {
        if (markers.isEmpty()) {
            return ""
        }

        return if (withSingleIcon != null) {
            handleMarkersWithSingleIcon(withSingleIcon)
        } else {
            handleMarkers()
        }
    }

    private fun handleMarkersWithSingleIcon(icon: Bitmap): String {
        val encoded = ImageHelper.encodeImageWithMime(icon)
        val sharedIconVar = "sharedIcon${markers[0].identifier}"

        return buildString {
            append("var $sharedIconVar = new Image();\n")
            append("$sharedIconVar.src = 'data:${encoded.mimeType};base64,${encoded.base64}';\n")
            markers.forEach { append(AddMarker(it, sharedIconVar).toJS()) }
        }
    }

    private fun handleMarkers(): String = markers.joinToString("\n") { AddMarker(it).toJS() }
}
