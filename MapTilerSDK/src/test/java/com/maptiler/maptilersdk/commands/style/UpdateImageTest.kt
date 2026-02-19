/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.style

import android.graphics.Bitmap
import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.helpers.EncodedImage
import com.maptiler.maptilersdk.helpers.ImageHelper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateImageTest {
    @Before
    fun setUp() {
        mockkObject(ImageHelper)
    }

    @After
    fun tearDown() {
        unmockkObject(ImageHelper)
    }

    @Test
    fun updateImageToJS_EncodesBitmapAndCallsUpdateImage() {
        val bmp = mockk<Bitmap>()
        // Mocking Bitmap methods if needed, though ImageHelper is mocked
        every { bmp.hasAlpha() } returns true

        every { ImageHelper.encodeImageWithMime(any()) } returns EncodedImage("UPDATED_IMG_DATA", "image/png")
        every { ImageHelper.getEncodedString(any()) } returns "data:image/png;base64,UPDATED_IMG_DATA"

        val identifier = "my-image"
        val js = UpdateImage(identifier, bmp).toJS()

        assertTrue(js.contains("${MTBridge.MAP_OBJECT}.updateImage('$identifier', __mtImg);"))
        assertTrue(js.contains("data:image/png;base64,UPDATED_IMG_DATA"))
        assertTrue(js.contains("__mtImg.src = 'data:image/png;base64,UPDATED_IMG_DATA';"))
    }
}
