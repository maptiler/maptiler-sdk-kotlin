/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.helpers

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

internal object ImageHelper {
    fun encodeImage(bm: Bitmap): String {
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
