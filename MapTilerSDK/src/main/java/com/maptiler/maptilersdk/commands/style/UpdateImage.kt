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

internal data class UpdateImage(
    val identifier: String,
    val image: Bitmap,
) : MTCommand {
    override val isPrimitiveReturnType: Boolean = false

    override fun toJS(): JSString {
        val encoded = ImageHelper.encodeImageWithMime(image)
        val dataUri = ImageHelper.getEncodedString(encoded)
        val sanitizedIdentifier = identifier.replace("", "").replace("'", "'")

        return """
            (function() {
                const __mtImg = new Image();
                __mtImg.src = '$dataUri';
                __mtImg.onload = function() {
                    ${MTBridge.MAP_OBJECT}.updateImage('$sanitizedIdentifier', __mtImg);
                };
            })();
            """.trimIndent()
    }
}
