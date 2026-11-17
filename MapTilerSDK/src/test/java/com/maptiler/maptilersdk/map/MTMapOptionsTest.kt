/*
 * Copyright (c) 2025, MapTiler
 * All rights reserved.
 * SPDX-License-Identifier: BSD 3-Clause
 */

package com.maptiler.maptilersdk.map

import com.maptiler.maptilersdk.helpers.JsonConfig
import com.maptiler.maptilersdk.map.types.MTSize
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Test

class MTMapOptionsTest {
    @Test
    fun `locale and maxCanvasSize serialize with map options`() {
        val options =
            MTMapOptions(
                locale = mapOf("en" to "English"),
                maxCanvasSize = MTSize(800, 600),
            )

        val jsonElement = JsonConfig.json.parseToJsonElement(JsonConfig.json.encodeToString(options)).jsonObject

        assertEquals("{\"en\":\"English\"}", jsonElement["locale"].toString())
        assertEquals("[800,600]", jsonElement["maxCanvasSize"].toString())
    }

    @Test
    fun `cooperativeGestures serializes with map options`() {
        val options = MTMapOptions(cooperativeGestures = true)

        val jsonElement = JsonConfig.json.parseToJsonElement(JsonConfig.json.encodeToString(options)).jsonObject

        assertEquals("true", jsonElement["cooperativeGestures"].toString())
    }
}
