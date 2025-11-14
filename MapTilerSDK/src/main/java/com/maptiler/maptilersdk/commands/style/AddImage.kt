/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import android.graphics.Bitmap
import com.maptiler.maptilersdk.bridge.JSString
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTCommand
import com.maptiler.maptilersdk.helpers.ImageHelper
import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.style.image.MTAddImageOptions
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

internal data class AddImage(
    val identifier: String,
    val image: Bitmap,
    val options: MTAddImageOptions? = null,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): JSString {
        val encoded = ImageHelper.encodeImageWithMime(image)
        val dataUri = ImageHelper.getEncodedString(encoded)
        val sanitizedIdentifier = identifier.replace("\\", "\\\\").replace("'", "\\'")
        val optionsJson =
            options?.let {
                val surrogate =
                    AddImageOptionsSurrogate(
                        pixelRatio = it.pixelRatio,
                        sdf = it.sdf,
                    )
                JsonConfig.json.encodeToString(surrogate)
            }

        val addImageCall =
            if (optionsJson != null) {
                "${MTBridge.MAP_OBJECT}.style.addImage('$sanitizedIdentifier', __mtImg, $optionsJson);"
            } else {
                "${MTBridge.MAP_OBJECT}.style.addImage('$sanitizedIdentifier', __mtImg);"
            }

        return """
            (function() {
                const __mtImg = new Image();
                __mtImg.src = '$dataUri';
                __mtImg.onload = function() {
                    $addImageCall
                };
            })();
        """.trimIndent()
    }
}

@Serializable
private data class AddImageOptionsSurrogate(
    val pixelRatio: Double? = null,
    val sdf: Boolean? = null,
)
