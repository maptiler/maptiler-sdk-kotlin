/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.ImageHelper
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.style.layer.MTLayer
import com.maptiler.maptilersdk.map.style.layer.symbol.MTSymbolLayer

internal data class AddLayer(
    val layer: MTLayer,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String =
        if (layer is MTSymbolLayer) {
            handleSymbolLayer(layer)
        } else {
            ""
        }

    private fun handleSymbolLayer(layer: MTSymbolLayer): JSString {
        val layerString: JSString = JsonConfig.json.encodeToString(layer)

        if (layer.icon != null) {
            val encodedImageString = ImageHelper.encodeImage(layer.icon!!)

            val iconString = """
            var icon${layer.identifier} = new Image();
                icon${layer.identifier}.src = 'data:image/png;base64,$encodedImageString';
                icon${layer.identifier}.onload = function() {
                    map.addImage('icon${layer.identifier}', icon${layer.identifier})
        """

            return """
                $iconString
                
                ${MTBridge.MAP_OBJECT}.addLayer($layerString)
                };
                
                """.trimIndent()
        } else {
            return ""
        }
    }
}
