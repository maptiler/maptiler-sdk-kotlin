/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

internal data class EncodedImage(
    val base64: String,
    val mimeType: String,
)

internal object ImageHelper {
    /**
     * Encode the given bitmap to Base64 and return the payload together with the correct MIME type.
     * - Uses PNG when the bitmap has alpha, otherwise JPEG.
     * - Uses Base64.NO_WRAP to avoid CR/LF in the output, which is important for embedding inside JS strings.
     */
    fun encodeImageWithMime(bm: Bitmap): EncodedImage {
        val baos = ByteArrayOutputStream()

        val (compressFormat, mime) =
            if (bm.hasAlpha()) {
                Bitmap.CompressFormat.PNG to "image/png"
            } else {
                Bitmap.CompressFormat.JPEG to "image/jpeg"
            }

        bm.compress(compressFormat, 100, baos)
        val byteArray = baos.toByteArray()

        val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        return EncodedImage(base64 = base64, mimeType = mime)
    }

    /**
     * Returns a data URI for the given encoded image payload.
     */
    fun getEncodedString(encodedImage: EncodedImage): String = "data:${encodedImage.mimeType};base64,${encodedImage.base64}"

    /**
     * Deprecated: prefer [encodeImageWithMime]. Kept for tests/backward-compatibility.
     */
    @Deprecated("Use encodeImageWithMime for proper MIME handling")
    fun encodeImage(bm: Bitmap): String = encodeImageWithMime(bm).base64
}
