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
import com.maptiler.maptilersdk.map.style.image.MTAddImageOptions
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddImageTest {
    @Before
    fun setUp() {
        mockkObject(ImageHelper)
    }

    @After
    fun tearDown() {
        unmockkObject(ImageHelper)
    }

    @Test
    fun addImageToJS_EncodesBitmapAndCallsAddImage_WithoutOptions() {
        val bmp = mockk<Bitmap>()
        every { bmp.hasAlpha() } returns true

        every { ImageHelper.encodeImageWithMime(any()) } returns EncodedImage("IMG_DATA", "image/png")
        every { ImageHelper.getEncodedString(any()) } returns "data:image/png;base64,IMG_DATA"

        val identifier = "my-image"
        val command = AddImage(identifier, bmp)

        assertFalse(command.isPrimitiveReturnType)

        val js = command.toJS()

        assertTrue(js.contains("__mtImg.src = 'data:image/png;base64,IMG_DATA';"))
        assertTrue(js.contains("${MTBridge.MAP_OBJECT}.style.addImage('$identifier', __mtImg);"))
    }

    @Test
    fun addImageToJS_EncodesBitmapAndCallsAddImage_WithOptions() {
        val bmp = mockk<Bitmap>()
        every { bmp.hasAlpha() } returns true

        every { ImageHelper.encodeImageWithMime(any()) } returns EncodedImage("IMG_DATA", "image/png")
        every { ImageHelper.getEncodedString(any()) } returns "data:image/png;base64,IMG_DATA"

        val identifier = "my-image"
        val options = MTAddImageOptions(sdf = true, pixelRatio = 2.0)
        val js = AddImage(identifier, bmp, options).toJS()

        assertTrue(js.contains("__mtImg.src = 'data:image/png;base64,IMG_DATA';"))
        assertTrue(js.contains("${MTBridge.MAP_OBJECT}.style.addImage('$identifier', __mtImg, {\"pixelRatio\":2.0,\"sdf\":true});"))
    }

    @Test
    fun addImageToJS_SanitizesIdentifier() {
        val bmp = mockk<Bitmap>()
        every { bmp.hasAlpha() } returns true

        every { ImageHelper.encodeImageWithMime(any()) } returns EncodedImage("IMG_DATA", "image/png")
        every { ImageHelper.getEncodedString(any()) } returns "data:image/png;base64,IMG_DATA"

        val identifier = "my'image\\with'quotes"
        val js = AddImage(identifier, bmp).toJS()

        assertTrue(js.contains("${MTBridge.MAP_OBJECT}.style.addImage('my\\'image\\\\with\\'quotes', __mtImg);"))
    }
}
