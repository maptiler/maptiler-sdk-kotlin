/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.commands.annotations

import android.graphics.Bitmap
import com.maptiler.maptilersdk.annotations.MTAnchor
import com.maptiler.maptilersdk.annotations.MTMarker
import com.maptiler.maptilersdk.helpers.EncodedImage
import com.maptiler.maptilersdk.helpers.ImageHelper
import com.maptiler.maptilersdk.map.LngLat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BatchMarkerCommandsTest {
    private fun markers(): List<MTMarker> =
        listOf(
            MTMarker(LngLat(10.0, 20.0)).apply {
                anchor = MTAnchor.LEFT
                offset = 2.0
            },
            MTMarker(LngLat(22.0, 12.0)).apply {
                anchor = MTAnchor.RIGHT
                offset = 3.0
            },
        )

    @Test
    fun addMarkersToJS_EmptyListReturnsEmptyString() {
        assertEquals("", AddMarkers(emptyList()).toJS())
    }

    @Test
    fun addMarkersToJS_AddsEveryMarkerWithOptionsAndEvents() {
        val markers = markers()
        val command = AddMarkers(markers)
        val js = command.toJS()

        assertFalse(command.isPrimitiveReturnType)
        markers.forEach { marker ->
            assertTrue(js.contains("const ${marker.identifier} = new maptilersdk.Marker({"))
            assertTrue(js.contains("anchor: '${marker.anchor.value}'"))
            assertTrue(js.contains("offset: ${marker.offset}"))
            assertTrue(js.contains(".setLngLat([${marker.coordinates.lng}, ${marker.coordinates.lat}])"))
            assertTrue(js.contains("${marker.identifier}.on('dragstart', handle${marker.identifier}DragEvent"))
            assertTrue(js.contains("${marker.identifier}.on('dragend', handle${marker.identifier}DragEvent"))
        }
    }

    @Test
    fun addMarkersToJS_WithSingleIcon_EncodesIconOnceAndSharesIt() {
        val bmp = mockk<Bitmap>()
        mockkObject(ImageHelper)
        every { ImageHelper.encodeImageWithMime(any()) } returns EncodedImage("AAA", "image/png")

        try {
            val markers = markers()
            val sharedIconVar = "sharedIcon${markers[0].identifier}"
            val js = AddMarkers(markers, bmp).toJS()

            verify(exactly = 1) { ImageHelper.encodeImageWithMime(bmp) }
            assertTrue(js.contains("$sharedIconVar.src = 'data:image/png;base64,AAA';"))
            markers.forEach { marker ->
                assertTrue(js.contains("icon${marker.identifier}.src = $sharedIconVar.src;"))
                assertTrue(js.contains("element: icon${marker.identifier}"))
            }
        } finally {
            unmockkObject(ImageHelper)
        }
    }

    @Test
    fun removeMarkersToJS_RemovesEveryMarker() {
        val markers = markers()
        val command = RemoveMarkers(markers)

        assertFalse(command.isPrimitiveReturnType)
        assertEquals(
            "if (typeof ${markers[0].identifier} !== 'undefined') { ${markers[0].identifier}.remove(); }\n" +
                "if (typeof ${markers[1].identifier} !== 'undefined') { ${markers[1].identifier}.remove(); }",
            command.toJS(),
        )
    }
}
