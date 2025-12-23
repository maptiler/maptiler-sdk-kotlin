/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.annotations

import com.maptiler.maptilersdk.bridge.MTBridge
import com.maptiler.maptilersdk.bridge.MTBridgeReturnType
import com.maptiler.maptilersdk.bridge.MTCommandExecutable
import com.maptiler.maptilersdk.commands.annotations.AddTextPopup
import com.maptiler.maptilersdk.commands.annotations.CloseTextPopup
import com.maptiler.maptilersdk.commands.annotations.GetTextPopupLngLat
import com.maptiler.maptilersdk.commands.annotations.IsTextPopupOpen
import com.maptiler.maptilersdk.commands.annotations.OpenTextPopup
import com.maptiler.maptilersdk.commands.annotations.SetMaxWidthToTextPopup
import com.maptiler.maptilersdk.commands.annotations.SetOffsetToTextPopup
import com.maptiler.maptilersdk.commands.annotations.SetSubpixelPositioningToTextPopup
import com.maptiler.maptilersdk.commands.annotations.SetTextToTextPopup
import com.maptiler.maptilersdk.commands.annotations.TrackTextPopupPointer
import com.maptiler.maptilersdk.map.LngLat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MTPopupTest {
    @Test
    fun getLngLatReturnsBridgeValue() =
        runBlocking {
            val popup = MTTextPopup(identifier = "popup1", _coordinates = LngLat(1.0, 2.0))
            val executor = mockk<MTCommandExecutable>()
            coEvery { executor.execute(any()) } returns
                MTBridgeReturnType.StringDoubleDict(mapOf("lng" to 3.0, "lat" to 4.0))

            val bridge = MTBridge(executor)
            popup.bindBridge(bridge)

            val result = popup.getLngLat()

            assertEquals(LngLat(3.0, 4.0), result)
            assertEquals(result, popup.coordinates)
            coVerify { executor.execute(match { it is GetTextPopupLngLat }) }
        }

    @Test
    fun refreshIsOpenUpdatesState() =
        runBlocking {
            val popup = MTTextPopup(identifier = "popup2", _coordinates = LngLat(0.0, 0.0))
            val executor = mockk<MTCommandExecutable>()
            coEvery { executor.execute(any()) } returns MTBridgeReturnType.BoolValue(true)

            val bridge = MTBridge(executor)
            popup.bindBridge(bridge)

            val isOpen = popup.refreshIsOpen()

            assertTrue(isOpen)
            assertTrue(popup.isOpen)
            coVerify { executor.execute(match { it is IsTextPopupOpen }) }
        }

    @Test
    fun addTextPopupToJSIncludesMaxWidth() {
        val popup = MTTextPopup(identifier = "popup3", _coordinates = LngLat(5.0, 6.0))
        popup.text = "Hello"
        popup.offset = 5.0
        popup.maxWidth = 300

        val js = AddTextPopup(popup).toJS().trimIndent()

        assertTrue(js.contains(".setMaxWidth(\"300px\")"))
        assertTrue(js.contains(".setLngLat([5.0, 6.0])"))
        assertTrue(js.contains(".setText(\"Hello\")"))
    }

    @Test
    fun addTextPopupIncludesSubpixelPositioningWhenProvided() {
        val popup = MTTextPopup(identifier = "popupSubpixel", _coordinates = LngLat(1.0, 1.0))
        popup.subpixelPositioning = true

        val js = AddTextPopup(popup).toJS()

        assertTrue(js.contains("subpixelPositioning: true"))
    }

    @Test
    fun addTextPopupAttachesOpenCloseEventHandlers() {
        val popup = MTTextPopup(identifier = "popupEvents", _coordinates = LngLat(2.0, 3.0))
        val js = AddTextPopup(popup).toJS()

        assertTrue(js.contains("popupEvents.on('open'"))
        assertTrue(js.contains("popupEvents.on('close'"))
        assertTrue(js.contains("Android.onEvent(\"popup.open\""))
        assertTrue(js.contains("Android.onEvent(\"popup.close\""))
    }

    @Test
    fun setMaxWidthCommandToJSMatchesSignature() {
        val popup = MTTextPopup(identifier = "popup4", _coordinates = LngLat(0.0, 0.0))
        popup.maxWidth = 250

        val js = SetMaxWidthToTextPopup(popup).toJS()

        assertEquals("popup4.setMaxWidth(\"250px\");", js)
    }

    @Test
    fun setOffsetCommandToJSMatchesSignature() {
        val popup = MTTextPopup(identifier = "popup5", _coordinates = LngLat(0.0, 0.0))

        val js = SetOffsetToTextPopup(popup, 12.5).toJS()

        assertEquals("popup5.setOffset(12.5);", js)
    }

    @Test
    fun setTextCommandToJSMatchesSignature() {
        val popup = MTTextPopup(identifier = "popup6", _coordinates = LngLat(0.0, 0.0))

        val js = SetTextToTextPopup(popup, "Updated").toJS()

        assertEquals("popup6.setText(\"Updated\");", js)
    }

    @Test
    fun setSubpixelPositioningCommandToJSMatchesSignature() {
        val popup = MTTextPopup(identifier = "popup7", _coordinates = LngLat(0.0, 0.0))

        val js = SetSubpixelPositioningToTextPopup(popup, false).toJS()

        assertEquals("popup7.setSubpixelPositioning(false);", js)
    }

    @Test
    fun trackPointerCommandToJSMatchesSignature() {
        val popup = MTTextPopup(identifier = "popup8", _coordinates = LngLat(0.0, 0.0))

        val js = TrackTextPopupPointer(popup).toJS()

        assertEquals("popup8.trackPointer();", js)
    }

    @Test
    fun openCommandToJSMatchesSignature() {
        val popup = MTTextPopup(identifier = "popupOpen", _coordinates = LngLat(0.0, 0.0))

        val js = OpenTextPopup(popup).toJS()

        assertEquals("popupOpen.addTo(map);", js)
    }

    @Test
    fun closeCommandToJSMatchesSignature() {
        val popup = MTTextPopup(identifier = "popupClose", _coordinates = LngLat(0.0, 0.0))

        val js = CloseTextPopup(popup).toJS()

        assertEquals("popupClose.remove();", js)
    }
}
