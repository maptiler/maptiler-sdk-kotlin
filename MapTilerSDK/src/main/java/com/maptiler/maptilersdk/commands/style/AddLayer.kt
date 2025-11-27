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
import com.maptiler.maptilersdk.map.style.layer.circle.MTCircleLayer
import com.maptiler.maptilersdk.map.style.layer.fill.MTFillLayer
import com.maptiler.maptilersdk.map.style.layer.line.MTLineLayer
import com.maptiler.maptilersdk.map.style.layer.raster.MTRasterLayer
import com.maptiler.maptilersdk.map.style.layer.symbol.MTSymbolLayer

internal data class AddLayer(
    val layer: MTLayer,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): String =
        if (layer is MTSymbolLayer) {
            handleSymbolLayer(layer)
        } else if (layer is MTFillLayer) {
            handleFillLayer(layer)
        } else if (layer is MTLineLayer) {
            handleLineLayer(layer)
        } else if (layer is MTRasterLayer) {
            handleRasterLayer(layer)
        } else if (layer is MTCircleLayer) {
            handleCircleLayer(layer)
        } else {
            // Fallback to a generic addLayer for any future-supported layer types
            val layerString: JSString = JsonConfig.json.encodeToString(layer)
            "${MTBridge.MAP_OBJECT}.addLayer($layerString);"
        }

    private fun handleSymbolLayer(layer: MTSymbolLayer): JSString {
        val layerString: JSString = JsonConfig.json.encodeToString(layer)

        if (layer.icon != null) {
            val encoded = ImageHelper.encodeImageWithMime(layer.icon!!)

            // Wrap in an IIFE and use a local variable name to avoid issues when
            // layer identifiers contain characters that are invalid in JS identifiers (e.g., hyphens).
            // Keep the image name used in the style as 'icon{identifier}' as before.
            return """
                (function() {
                    const __mtImg = new Image();
                    __mtImg.src = 'data:${encoded.mimeType};base64,${encoded.base64}';
                    __mtImg.onload = function() {
                        ${MTBridge.MAP_OBJECT}.addImage('icon${layer.identifier}', __mtImg);
                        ${MTBridge.MAP_OBJECT}.addLayer($layerString);
                    };
                })();
                """.trimIndent()
        } else {
            // No icon to register; add the layer directly
            return "${MTBridge.MAP_OBJECT}.addLayer($layerString);"
        }
    }

    private fun handleFillLayer(layer: MTFillLayer): JSString {
        val layerString: JSString = JsonConfig.json.encodeToString(layer)

        return "${MTBridge.MAP_OBJECT}.addLayer($layerString);"
    }

    private fun handleLineLayer(layer: MTLineLayer): JSString {
        val layerString: JSString = JsonConfig.json.encodeToString(layer)

        return "${MTBridge.MAP_OBJECT}.addLayer($layerString);"
    }

    private fun handleRasterLayer(layer: MTRasterLayer): JSString {
        val layerString: JSString = JsonConfig.json.encodeToString(layer)

        return "${MTBridge.MAP_OBJECT}.addLayer($layerString);"
    }

    private fun handleCircleLayer(layer: MTCircleLayer): JSString {
        val layerString: JSString = JsonConfig.json.encodeToString(layer)

        return "${MTBridge.MAP_OBJECT}.addLayer($layerString);"
    }
}
